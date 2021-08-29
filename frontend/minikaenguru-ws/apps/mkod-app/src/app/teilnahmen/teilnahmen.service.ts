import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Anmeldungen } from '../shared/beteiligungen.model';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';

@Injectable({
	providedIn: 'root'
})
export class TeilnahmenService {

	constructor(private http: HttpClient) { }

	public loadTeilnahmen(jahr: number): Observable<Anmeldungen> {

		const url = environment.apiUrl + '/open-data/statistik/teilnahmen?jahr=' + jahr;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}
}
