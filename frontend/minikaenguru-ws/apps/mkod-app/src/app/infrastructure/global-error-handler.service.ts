import { ErrorHandler, Injector, Injectable, Inject } from '@angular/core';
import { LogService } from '@minikaenguru-ws/common-logging';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { environment } from '../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { LogPublishersService } from './log-publishers.service';
import { Router } from '@angular/router';
import { STORAGE_KEY_USER, STORAGE_KEY_INVALID_SESSION } from '@minikaenguru-ws/common-auth';

@Injectable(
	{
		providedIn: 'root'
	}
)
export class GlobalErrorHandlerService implements ErrorHandler {

	private logger: LogService;
	private messageService: MessageService;
	private router: Router;

	constructor(private injector: Injector) {

		// ErrorHandler wird vor allen anderen Injectables instanziiert,
		// so dass man benötigte Services nicht im Konstruktor injekten kann !!!

		const logPublishersService = this.injector.get(LogPublishersService);
		this.logger = this.injector.get(LogService);

		this.logger.initLevel(environment.loglevel);
		this.logger.registerPublishers(logPublishersService.publishers);
		this.logger.info('logging initialized: loglevel=' + environment.loglevel);

		this.messageService = this.injector.get(MessageService);

		this.router = this.injector.get(Router);
	}


	handleError(error: any): void {

		if (error instanceof HttpErrorResponse) {
			const httpError = error as HttpErrorResponse;
			console.log('HttpErrorResponse: ' + httpError.status);
			this.handleHttpError(httpError);
		} else {
			console.log('ErrorEvent: ' + error);
			if (error.stack) {
				console.log(error.stack);
			}
			let msg = 'mkod-app: Unerwarteter Fehler: ' + error.message;

			if (error.stack) {
				msg += ' - ' + error.stack;
			}
			this.logger.error(msg);
			if (environment.envName === 'DEV') {
				this.showServerResponseMessage('ERROR', 'Unerwarteter GUI-Error: ' + error.message);
			}
		}
	}

	public handleHttpError(httpError: HttpErrorResponse) {
		if (httpError.status === 0) {
			this.messageService.error('Der Server ist nicht erreichbar.');
		} else {
			const msg = this.extractMessageObject(httpError);
			switch (httpError.status) {
				case 403:
					localStorage.setItem(STORAGE_KEY_INVALID_SESSION, JSON.stringify({ level: 'ERROR', message: 'Sie haben keine Berechtigung, diese Resource aufzurufen.' }));
					this.router.navigateByUrl('/timeout');
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


	public extractMessageObject(error: HttpErrorResponse): { level: string, message: string } {

		if (error.error && error.error.message) {
			return { level: 'ERROR', message: error.error.message['message'] };
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

