import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Veranstalter, VeranstalterSuchanfrage } from './veranstalter.model';

@Injectable({
	providedIn: 'root'
})
export class VeranstalterService {


	constructor(private http: HttpClient) { }


	public findVeranstalter(suchanfrage: VeranstalterSuchanfrage): Observable<Veranstalter[]> {


		const url = environment.apiUrl + '/veranstalter/suche';

		return this.http.post(url, suchanfrage, {observe: 'body'}).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public loadVeranstalterDetails(uuid: string): Observable<Veranstalter> {


		const url = environment.apiUrl + '/veranstalter/' + uuid;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

}
