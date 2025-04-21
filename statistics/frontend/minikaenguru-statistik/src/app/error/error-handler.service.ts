import { HttpErrorResponse } from "@angular/common/http";
import { ErrorHandler, Injectable, Injector, inject } from "@angular/core";
import { extractServerErrorMessage, getHttpErrorResponse } from "@mks/http";
import { MessageService } from "@mks/messages-api";
import { Configuration } from "../shared/config/src/lib/config/configuration";
import { Router } from "@angular/router";


@Injectable({
    providedIn: 'root'
})
export class ErrorHandlerService implements ErrorHandler {

    #messageService = inject(MessageService);
    #configuration = inject(Configuration);
    #router = inject(Router);
    

    handleError(error: NonNullable<unknown>): void {

        const httpErrorResponse: HttpErrorResponse | undefined = getHttpErrorResponse(error);

        if (httpErrorResponse === undefined) {
            this.#handleAnyOtherError(error)
        } else {
            this.#handleHttpError(httpErrorResponse);
        }
    }

    #handleHttpError(httpErrorResponse: HttpErrorResponse): void {

        const message = extractServerErrorMessage(httpErrorResponse);
        if (message.level === 'WARN') {
            this.#messageService.warn(message.message);
        } else {
            if (httpErrorResponse.status === 404) {
                this.#router.navigateByUrl('error');
                this.#messageService.error('Diesen Wettbewerb gibt es nicht.')
            } else {
                this.#messageService.error(message.message);   
            }                     
        }
    }

    #handleAnyOtherError(error: unknown): void {

        this.#messageService.error('Upsi, da ist ein unerwarteter Fehler aufgetreten. Bitte sende eine Mail an minikaenguru(at)egladil.de, am Besten mit Screenshot');


        if (!this.#configuration.production) {
            console.error(error);
            // hier mal schauen, wie nötig ein logging endpoint in der API ist
        }

        
    }
}
