import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';
import { LogService, Message, WARN, ERROR, INFO } from 'hewi-ng-lib';

@Injectable(
	{ providedIn: 'root' }
)
export class HttpErrorHandler {

	constructor(private messageService: MessageService, private logger: LogService) { }

	handleError(error: HttpErrorResponse, context: string) {
		if (error instanceof ErrorEvent) {
			this.logger.error(context + ': ErrorEvent occured - ' + JSON.stringify(error));
			// throw (error);
		} else {

			const httpError: HttpErrorResponse = error as HttpErrorResponse;

			if (httpError.status === 0) {
				this.messageService.add({ severity: 'error', summary: 'Der Server ist nicht erreichbar.' });
			} else {

				// Zum Testen des GlobalErrorHandlers einkommentieren und vom Server einen error senden.
				// const theBody = JSON.parse(error.error);

				const msg = error.error.message as Message;
				switch (error.status) {
					case 401:
					case 908:
						this.showServerResponseMessage(msg);
						// this.sessionService.clearSession();
						break;
					default: {
						if (msg) {
							this.showServerResponseMessage(msg);
						} else {
							this.messageService.add({ severity: 'error', summary: 'Es ist ein unerwarteter Fehler aufgetreten.', detail: 'Bitte senden Sie eine Mail an info@egladil.de' });
						}
					}
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
				this.messageService.add({ severity: 'warn', summary: msg.message });
				break;
			case ERROR:
				this.messageService.add({ severity: 'error', summary: msg.message });
				break;
			default:
				this.messageService.add({ severity: 'error',
					summary: 'Unbekanntes message.level ' + msg.level + ' vom Server bekommen.',
					detail: msg.message });
		}
	}
}



