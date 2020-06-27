import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Lehrer } from '../lehrer/lehrer.model';
import { environment } from '../../environments/environment';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { Privatveranstalter } from '../privatveranstalter/privatveranstalter.model';


@Injectable({
	providedIn: 'root'
})
export class VeranstalterService {

	constructor(private http: HttpClient) { }

	public loadLehrer(): Observable<Lehrer> {

		const url = environment.apiUrl + '/wettbewerb/veranstalter/lehrer';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}

	getZugangsstatusUnterlagen(): Observable<boolean> {

		const url = environment.apiUrl + '/wettbewerb/veranstalter/zugangsstatus';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public loadPrivatveranstalter(): Observable<Privatveranstalter> {

		const url = environment.apiUrl + '/wettbewerb/veranstalter/privat';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}
}
