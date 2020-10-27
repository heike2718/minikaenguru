import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';
import { Kind } from '@minikaenguru-ws/common-components';


@Injectable({
	providedIn: 'root'
})
export class PrivatauswertungService {


	constructor(private http: HttpClient) { }


	public loadKinder(): Observable<Kind[]> {

		const url = environment.apiUrl + '/privatkinder';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

};
