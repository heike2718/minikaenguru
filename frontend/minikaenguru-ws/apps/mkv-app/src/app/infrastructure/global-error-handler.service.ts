import { ErrorHandler, Injector, Injectable } from '@angular/core';
import { LogService } from '@minikaenguru-ws/common-logging';
import { environment } from '../../environments/environment';
import { HttpErrorResponse } from '@angular/common/http';
import { LogPublishersService } from './log-publishers.service';

@Injectable(
	{
		providedIn: 'root'
	}
)
export class GlobalErrorHandlerService implements ErrorHandler {

	private logService: LogService;

	constructor(injector: Injector) {
	}

	// constructor(private injector: Injector) {

    //     // ErrorHandler wird vor allen anderen Injectables instanziiert,
	// 	// so dass man benötigte Services nicht im Konstruktor injekten kann !!!

	// 	const logPublishersService = this.injector.get(LogPublishersService);
	// 	this.logService = this.injector.get(LogService);

	// 	this.logService.initLevel(environment.loglevel);
	// 	this.logService.registerPublishers(logPublishersService.publishers);
	// 	this.logService.info('logging initialized: loglevel=' + environment.loglevel);


	// }

	handleError(error: any): void {

		// let message = 'Checklistenapp: unerwarteter Fehler aufgetreten: ';

		// if (error.message) {
		// 	message += error.message;
		// }

		// // try sending an Error-Log to the Server
		// // TODO in Session legen
		// const STORAGE_KEY_ID_REFERENCE = 'mkv-app-id-reference';

		// const idReference = sessionStorage.getItem(STORAGE_KEY_ID_REFERENCE);

		// this.logService.error(message + ' (idRef=' + idReference + ')');

		// if (error instanceof HttpErrorResponse) {
		// 	this.logService.debug('das sollte nicht vorkommen, da diese Errors einem der services behandelt werden');
		// } else {
		// 	this.logService.error('Unerwarteter Fehler: ' + error.message + ' (idRef=' + idReference + ')');
		// }
	}
}

