import { HttpErrorResponse } from "@angular/common/http";
import { ErrorHandler, Injectable, Injector } from "@angular/core";
import { extractServerErrorMessage, getHttpErrorResponse } from "@mkbiza-app/http";
import { MessageService } from "@mkbiza-app/messages-api";
import { Configuration } from "../shared/config/src/lib/config/configuration";


@Injectable({
    providedIn: 'root'
})
export class ErrorHandlerService implements ErrorHandler {

    constructor(private injector: Injector) { }

    handleError(error: NonNullable<unknown>): void {

        const messageService = this.injector.get(MessageService);
        

        const httpErrorResponse: HttpErrorResponse | undefined = getHttpErrorResponse(error);

        if (httpErrorResponse === undefined) {
            this.#handleAnyOtherError(error, messageService)
        } else {
            this.#handleHttpError(httpErrorResponse, messageService);
        }
    }

    #handleHttpError(httpErrorResponse: HttpErrorResponse, messageService: MessageService): void {

        const message = extractServerErrorMessage(httpErrorResponse);
        if (message.level === 'WARN') {
            messageService.warn(message.message);
        } else {
            messageService.error(message.message);
        }
    }

    #handleAnyOtherError(error: unknown, messageService: MessageService): void {
        
        messageService.error('Upsi, da ist ein unerwarteter Fehler aufgetreten. Bitte sende eine Mail an minikaenguru(at)egladil.de, am Besten mit Screenshot');
       
        const configuraton = this.injector.get(Configuration);

        if (!configuraton.production) {
            console.error(error);
            // hier mal schauen, wie nötig ein logging endpoint in der API ist
        }       
    }
}
