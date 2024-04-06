import { ErrorHandler, Injectable, Injector } from "@angular/core";


@Injectable({
    providedIn: 'root'
})
export class ErrorHandlerService implements ErrorHandler {

    constructor(private injector: Injector) { }

    handleError(error: NonNullable<unknown>): void {

        
    }

    
}
