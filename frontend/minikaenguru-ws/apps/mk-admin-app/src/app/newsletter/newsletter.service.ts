import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload, Message } from '@minikaenguru-ws/common-messages';
import { Newsletter, NewsletterVersandauftrag, Versandinfo } from './newsletter.model';

@Injectable({
	providedIn: 'root'
})
export class NewsletterService {


	constructor(private http: HttpClient,
		private logger: LogService) { }



	public loadNewsletters(): Observable<Newsletter[]> {

		const url = environment.apiUrl + '/newsletters';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}

	public addNewsletter(newsletter: Newsletter): Observable<Newsletter> {

		const url = environment.apiUrl + '/newsletters';

		return this.http.post(url, newsletter).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public updateNewsletter(newsletter: Newsletter): Observable<Newsletter> {

		const url = environment.apiUrl + '/newsletters';

		return this.http.put(url, newsletter).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public deleteNewsletter(uuid: string): Observable<Message> {

		const url = environment.apiUrl + '/newsletters/' + uuid;

		return this.http.delete(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.message)
		);

	}

	public scheduleMailversand(auftrag: NewsletterVersandauftrag): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/newsletterversand';

		return this.http.post(url, auftrag).pipe(
			map(body => body as ResponsePayload)
		);

	}

	public getStatusNewsletterversand(versandinfo: Versandinfo): Observable<Versandinfo> {

		const url = environment.apiUrl + '/newsletterversand/' + versandinfo.uuid;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => {

				if (payload.message.level === 'INFO') {
					return payload.data
				}

				return of(undefined);
			})
		);

	}
};
