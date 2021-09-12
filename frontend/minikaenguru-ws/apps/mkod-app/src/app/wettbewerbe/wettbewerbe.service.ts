import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Wettbewerb } from './wettbewerb.model';

@Injectable({
	providedIn: 'root'
})
export class WettbewerbeService {

	constructor(private http: HttpClient) { }

	public loadWettbewerbe(): Observable<Wettbewerb[]> {

		const url = environment.apiUrl + '/wettbewerbe';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}
}
