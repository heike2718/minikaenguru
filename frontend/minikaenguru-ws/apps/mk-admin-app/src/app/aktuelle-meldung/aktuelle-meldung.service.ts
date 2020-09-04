import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { AktuelleMeldung } from './aktuelle-meldung.model';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class AktuelleMeldungService {

	private url = environment.apiUrl + '/meldungen/aktuelle-meldung';

	constructor(private http: HttpClient) { }

	public loadAktuelleMeldung(): Observable<AktuelleMeldung> {

		return this.http.get(this.url, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		)

	}

	public saveAktuelleMeldung(meldung: AktuelleMeldung): Observable<ResponsePayload> {


		return this.http.post(this.url, meldung, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);


	}

	public deleteAktuelleMeldung(): Observable<ResponsePayload> {

		return this.http.delete(this.url, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);

	}
}
