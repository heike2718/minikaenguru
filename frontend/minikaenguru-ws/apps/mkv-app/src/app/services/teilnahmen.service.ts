import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Wettbewerb, AnonymisierteTeilnahme } from '../wettbewerb/wettbewerb.model';
import { Schule, SchulanmeldungRequestPayload } from '../lehrer/schulen/schulen.model';

@Injectable({
	providedIn: 'root'
})
export class TeilnahmenService {

	constructor(private http: HttpClient, private logger: LogService) { }


	public getAktuellenWettbewerb(): Observable<Wettbewerb> {


		const url = environment.apiUrl + '/wettbewerb/aktueller';

		this.logger.debug('[TeilnahmenService] getAktuellenWettbewerb - url = ' + url);

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}

	public privatveranstalterAnmelden(): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/veranstalter/teilnahmen/privat';

		return this.http.post(url, null).pipe(
			map(body => body as ResponsePayload)
		);
	}

	public schuleAnmelden(schule: Schule): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/veranstalter/teilnahmen/schule';

		const payload: SchulanmeldungRequestPayload = {
			schulkuerzel: schule.kuerzel,
			schulname: schule.name
		};

		return this.http.post(url, payload).pipe(
			map(body => body as ResponsePayload)
		);
	}

	public ladeAnonymisierteTeilnahmen(teilnahmenummer: string): Observable<AnonymisierteTeilnahme[]> {

		const url = environment.apiUrl + '/teilnahmen/' + teilnahmenummer;

		return this.http.get(url, {observe: 'body'}).pipe(
			map(body => body as ResponsePayload),
			map(responsePayload => responsePayload.data as AnonymisierteTeilnahme[])
		);
	}
}
