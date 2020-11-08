import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Klasse } from '@minikaenguru-ws/common-components';

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

}
