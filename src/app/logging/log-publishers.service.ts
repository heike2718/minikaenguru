import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { LogPublisher, LogConsole, LogWebApi, LogService } from 'hewi-ng-lib';


@Injectable({
	providedIn: 'root'
})
export class LogPublishersService {

	publishers: LogPublisher[] = [];

	constructor(private http: HttpClient, private logservice: LogService) {

		this.initLoglevel();
		this.buildPublishers();
	}

	private buildPublishers(): void {

		if (environment.consoleLogActive) {
			this.publishers.push(new LogConsole());
		}

		// Create instance of LogWebApi Class
		if (environment.serverLogActive) {
			const url = environment.apiUrl + '/log';
			this.publishers.push(new LogWebApi(this.http, url));
		}
	}

	private initLoglevel(): void {
		this.logservice.initLevel(environment.loglevel);
	}

}
