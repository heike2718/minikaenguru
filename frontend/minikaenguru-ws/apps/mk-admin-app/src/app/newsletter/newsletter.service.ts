import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload, Message } from '@minikaenguru-ws/common-messages';
import { Newsletter, NewsletterVersandauftrag, Versandinfo } from './newsletter.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class NewsletterService {


	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public loadNewsletters(): Observable<Newsletter[]> {

		const url = environment.apiUrl + '/newsletters';

		const obs$ = this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);

	}

	public addNewsletter(newsletter: Newsletter): Observable<Newsletter> {

		const url = environment.apiUrl + '/newsletters';

		const obs$ = this.http.post(url, newsletter).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public updateNewsletter(newsletter: Newsletter): Observable<Newsletter> {

		const url = environment.apiUrl + '/newsletters';

		const obs$ = this.http.put(url, newsletter).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public deleteNewsletter(uuid: string): Observable<Message> {

		const url = environment.apiUrl + '/newsletters/' + uuid;

		const obs$ = this.http.delete(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.message)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);

	}

	public scheduleMailversand(auftrag: NewsletterVersandauftrag): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/newsletterversand';

		const obs$ = this.http.post(url, auftrag).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);

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
