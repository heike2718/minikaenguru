import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Klasse, KlasseRequestData } from '@minikaenguru-ws/common-components';

@Injectable({
	providedIn: 'root'
})
export class KlassenService {

	constructor(private http: HttpClient) { }


	public loadKlassen(schulkuerzel: string): Observable<Klasse[]> {

		const url = environment.apiUrl + '/klassen/' + schulkuerzel;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public insertKlasse(klasseRequestData: KlasseRequestData): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/klassen';

		return this.http.post(url, klasseRequestData).pipe(
			map(body => body as ResponsePayload)
		);

	}

	public updateKlasse(klasseRequestData: KlasseRequestData): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/klassen';

		return this.http.put(url, klasseRequestData).pipe(
			map(body => body as ResponsePayload)
		);

	}

	public deleteKlasse(uuid: string): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/klassen/' + uuid;

		return this.http.delete(url).pipe(
			map(body => body as ResponsePayload)
		);

	}

}
