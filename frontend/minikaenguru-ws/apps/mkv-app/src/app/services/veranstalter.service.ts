import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { Privatveranstalter } from '../wettbewerb/wettbewerb.model';


@Injectable({
	providedIn: 'root'
})
export class VeranstalterService {

	constructor(private http: HttpClient) { }

	getZugangsstatusUnterlagen(): Observable<boolean> {

		const url = environment.apiUrl + '/veranstalter/zugangsstatus';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public loadPrivatveranstalter(): Observable<Privatveranstalter> {

		const url = environment.apiUrl + '/veranstalter/privat';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

	}
}
