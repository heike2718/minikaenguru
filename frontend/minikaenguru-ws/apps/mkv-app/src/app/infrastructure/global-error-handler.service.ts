import { ErrorHandler, Injector, Injectable } from '@angular/core';
import { LogService } from '@minikaenguru-ws/common-logging';
import { ResponsePayload, MessageService } from '@minikaenguru-ws/common-messages';
import { environment } from '../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { LogPublishersService } from './log-publishers.service';

@Injectable(
	{
		providedIn: 'root'
	}
)
export class GlobalErrorHandlerService implements ErrorHandler {

	private logger: LogService;
	private messageService: MessageService;

	constructor(private injector: Injector) {

		// ErrorHandler wird vor allen anderen Injectables instanziiert,
		// so dass man benötigte Services nicht im Konstruktor injekten kann !!!

		const logPublishersService = this.injector.get(LogPublishersService);
		this.logger = this.injector.get(LogService);

		this.logger.initLevel(environment.loglevel);
		this.logger.registerPublishers(logPublishersService.publishers);
		this.logger.info('logging initialized: loglevel=' + environment.loglevel);

		this.messageService = this.injector.get(MessageService);


	}


	handleError(error: any): void {

		if (error instanceof HttpErrorResponse) {
			const httpError = error as HttpErrorResponse;
			console.log('HttpErrorResponse: ' + httpError.status);
			this.handleHttpError(httpError, 'mkv-app');
		} else {
			console.log('ErrorEvent: ' + error);
			this.logger.error('mkv-app: Unerwarteter Fehler: ' + error.message);
			this.showServerResponseMessage('ERROR', 'Unerwarteter GUI-Error: ' + error.message);
		}
	}

	public handleHttpError(httpError: HttpErrorResponse, context: string) {
		if (httpError.status === 0) {
			this.messageService.error('Der Server ist nicht erreichbar.');
		} else {
			const msg = this.extractMessageObject(httpError);
			switch (httpError.status) {
				case 401:
				case 908:
					this.messageService.error('Sie haben keine Berechtigung. Bitte loggen Sie sich ein.');
					break;
				default: {
					if (msg) {
						this.showServerResponseMessage(msg.level, msg.message);
					} else {
						this.messageService.error('Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de');
					}
				}
			}
		}
	}

	private extractMessageObject(error: HttpErrorResponse): { level: string, message: string } {

		if (error.error && error.error.message) {
			return { level: 'ERROR', message: error.error.message['message'] };
		}

		return null;
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

