import { Observable, of } from 'rxjs';
import { LogEntry } from './entities';
import { HttpClient } from '@angular/common/http';
import { publishLast, refCount } from 'rxjs/operators';

export abstract class LogPublisher {
	location: string;

	abstract log(record: LogEntry): Observable<boolean>;
	abstract clear(): Observable<boolean>;
}

export class LogConsole extends LogPublisher {

	log(entry: LogEntry): Observable<boolean> {

		console.log(entry.getLevel() + ' - ' + entry.getMessage());
		return of(true);
	}

	clear(): Observable<boolean> {
		console.clear();
		return of(true);
	}
}

export class LogWebApi extends LogPublisher {

	constructor(private http: HttpClient, private url: string) {
		super();
	}


	log(entry: LogEntry): Observable<boolean> {

		console.log('[LogWebApi]: ' + this.url);

		if (!this.url) {
			console.log('[LogWebApi]: muss mal die LogWebApi-Config prüfen');
			return of(false);
		}

		this.http.post(this.url, entry).pipe(
			publishLast(),
			refCount()
		).subscribe(
			() => {
				return of(true);
			},
			((error) => {
				this.handleErrors(error);
				return of(true);
			}));

		return of(true);
	}

	clear(): Observable<boolean> {
		// nothing to remove from serverlog
		return of(true);
	}

	private handleErrors(error: any): void {
		const errors: string[] = [];
		let msg = '';

		msg = 'Status: ' + error.status;
		msg += ' - Status Text: ' + error.statusText;
		errors.push(msg);

		console.error('Unerwarteter Fehler beim POST eines Client-Errors: ', errors);
	}

}


