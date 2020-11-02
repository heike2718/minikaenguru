import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';
import { Kind, Duplikatwarnung, PrivatkindRequestData } from '@minikaenguru-ws/common-components';


@Injectable({
	providedIn: 'root'
})
export class PrivatauswertungService {


	constructor(private http: HttpClient) { }


	public loadKinder(teilnahmenummer: string): Observable<Kind[]> {

		const url = environment.apiUrl + '/kinder/' + teilnahmenummer;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public checkDuplikat(data: PrivatkindRequestData): Observable<Duplikatwarnung> {

		const url = environment.apiUrl + '/kinder/duplikate';

		return this.http.post(url, data, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public insertKind(data: PrivatkindRequestData): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/kinder';

		return this.http.post(url, data, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);

	}

	public updateKind(data: PrivatkindRequestData): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/kinder';

		return this.http.put(url, data, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);
	}


	public deleteKind(uuid: string): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/kinder/' + uuid;

		return this.http.delete(url, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);
	}

};
