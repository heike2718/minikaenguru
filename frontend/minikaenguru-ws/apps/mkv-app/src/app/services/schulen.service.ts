import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Schule } from '../lehrer/schulen/schulen.model';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';


@Injectable({
	providedIn: 'root'
})
export class SchulenService {

	constructor(private http: HttpClient) { }


	findSchulen(): Observable<Schule[]> {

		const url = environment.apiUrl + '/veranstalter/lehrer/schulen';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public loadDetails(schulkuerzel: string): Observable<Schule> {

		const url = environment.apiUrl + '/veranstalter/lehrer/schulen/' + schulkuerzel + '/details';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}

}
