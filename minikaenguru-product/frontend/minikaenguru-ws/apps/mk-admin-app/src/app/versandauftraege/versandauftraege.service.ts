import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { NewsletterVersandauftrag, Versandauftrag } from '../shared/newsletter-versandauftrage.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class VersandauftragService {


	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public loadVersandauftraege(): Observable<Versandauftrag[]> {

		const url = environment.apiUrl + '/versandauftraege';

		const obs$ = this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);

	}

	public getStatusNewsletterversand(versandauftrag: Versandauftrag): Observable<Versandauftrag> {

		const url = environment.apiUrl + '/versandauftraege/' + versandauftrag.uuid;

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

	public scheduleMailversand(auftrag: NewsletterVersandauftrag): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/versandauftraege';

		const obs$ = this.http.post(url, auftrag).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);

	}
};
