import { Injectable, ErrorHandler, Injector, NgZone } from '@angular/core';
import { LogService } from 'hewi-ng-lib';
import { LogPublishersService } from '../logging/log-publishers.service';
import { environment } from 'src/environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { MessageService } from 'primeng/api';


@Injectable({
	providedIn: 'root'
})
export class GlobalErrorHandlerService implements ErrorHandler {

	private logService: LogService;

	private messageService: MessageService;

	constructor(private injector: Injector, private readonly ngZone: NgZone) {

		// ErrorHandler wird vor allen anderen Injectables instanziiert,
		// so dass man benötigte Services nicht im Konstruktor injekten kann !!!
		const logPublishersService = this.injector.get(LogPublishersService);
		this.logService = this.injector.get(LogService);

		this.logService.initLevel(environment.loglevel);
		this.logService.registerPublishers(logPublishersService.publishers);
		this.logService.info('logging initialized: loglevel=' + environment.loglevel);

		this.messageService = this.injector.get(MessageService);
		this.logService.info('MessageService initialized.');

	}

	handleError(error: any): void {

		let message = 'Es ist ein unerwarteter Fehler aufgetreten: ';

		if (error.message) {
			message += ' ' + error.message;
		}

		// ErrorHandler läuft außerhalb der Angular-Zone. message ist daher erst bei der nächsten Aktualisierung der Komponente sichtbar.
		// Das Anzeigen der Message muss daher mit ngZone.run() getriggert werden.
		// siehe https://t2informatik.de/blog/softwareentwicklung/fehlerbehandlung-in-angular-anwendungen/
		this.ngZone.run(() => this.messageService.add({ severity: 'error', summary: message }));

		if (error instanceof HttpErrorResponse) {
			this.logService.debug('das sollte nicht vorkommen, da diese Errors von einem der services behandelt werden');
		} else {
			// try sending an Error-Log to the Server
			// this.logService.error(message, this.sessionService.getClientAccessToken());
			this.logService.error('mkadmin-app - ' + message);
		}
	}
}
