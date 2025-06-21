import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { AktuelleMeldung } from './aktuelle-meldung.model';
import { Observable } from 'rxjs';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class AktuelleMeldungService {

	private url = environment.apiUrl + '/meldungen/aktuelle-meldung';

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public loadAktuelleMeldung(): Observable<AktuelleMeldung> {

		const obs$ = this.http.get(this.url, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public saveAktuelleMeldung(meldung: AktuelleMeldung): Observable<ResponsePayload> {

		const obs$ = this.http.post(this.url, meldung, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public deleteAktuelleMeldung(): Observable<ResponsePayload> {

		const obs$ = this.http.delete(this.url, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}
}
