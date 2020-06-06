import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { LogService } from '@minikaenguru-ws/common-logging';
import { MessageService, Message } from '@minikaenguru-ws/common-messages';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { Store } from '@ngrx/store';
import { searchError } from '../+state/schulkatalog.actions';
@Injectable(
	{
		providedIn: 'root'
	}
)
export class ErrorHandlerService {

	constructor(private store: Store<SchulkatalogState>, private logger: LogService, private messageService: MessageService) { }

	public handleError(error: HttpErrorResponse | Error, context: string) {

		this.logger.error(context + ': Error occured - ' + JSON.stringify(error));

		if (error instanceof HttpErrorResponse) {
			if (error.status === 0) {
				this.messageService.error('Der Server ist nicht erreichbar.');
			} else {
				const msg = this.extractMessageObject(error);
				switch (error.status) {
					case 401:
					case 908:
						this.messageService.error('Sie haben keine Berechtigung. Bitte loggen Sie sich ein.');
						break;
					default: {
						if (msg) {
							this.showServerResponseMessage(msg.level, msg.message);
							this.store.dispatch(searchError());
						} else {
							this.messageService.error('Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de');
						}
					}
				}
			}
		} else {
			this.logger.error('common-schulkatalog: Unerwarteter Fehler: ' + error.message);
			this.messageService.error('Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de');
		}
	}

	private extractMessageObject(error: HttpErrorResponse): { level: string, message: string } {

		if (error.error && error.error.message) {
			const errorMessage: Message = error.error['message']
			return { level: errorMessage.level, message: errorMessage.message };
		}

		if (error.error && error.message) {
			return { level: 'ERROR', message: error['message'] };
		}

		return { level: 'ERROR', message: 'Da ist im Backend irgendwas unerwartetes schiefgelaufen. Gugstu mal ins Log' };
	}

	private showServerResponseMessage(level: string, message: string) {

		switch (level) {
			case 'WARN':
				this.messageService.error(message);
				break;
			case 'ERROR':
				this.messageService.error(message);
				break;
			default:
				this.messageService.error('Unbekanntes message.level ' + level + ' vom Server bekommen.');
		}
	}
}
