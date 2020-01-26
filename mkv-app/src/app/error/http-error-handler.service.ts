import { MessagesService, LogService, Message, WARN, ERROR, INFO } from 'hewi-ng-lib';
import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable(
    { providedIn: 'root' }
)
export class HttpErrorHandlerService {


    constructor(private messagesService: MessagesService, private logger: LogService) { }

    handleError(error: HttpErrorResponse, context: string) {

        if (error instanceof ErrorEvent) {
            this.logger.error(context + ': ErrorEvent aufgetreten - ' + JSON.stringify(error));
        } else {

            const httpError: HttpErrorResponse = error as HttpErrorResponse;

            if (httpError.status === 0) {
                this.messagesService.error('Der Server ist nicht erreichbar. Bitte senden Sie eine Mail an info@egladil.de.');
            } else {

                const msg = error.error.message as Message;

                switch (error.status) {
                    case 401:
                    case 908:

                }
            }

        }

    }

    private showServerResponseMessage(msg: Message) {
        switch (msg.level) {
            case INFO:
                this.logger.debug('level INFO ist unerwartet.');
                break;
            case WARN:
                this.messagesService.warn(msg.message);
                break;
            case ERROR:
                this.messagesService.error(msg.message);
                break;
            default:
                this.messagesService.error('Unbekanntes message.level ' + msg.level + ' vom Server bekommen: ' + msg.message);
        }
    }

}
