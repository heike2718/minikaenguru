import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';
import { Kind, Duplikatwarnung, KindRequestData } from '@minikaenguru-ws/common-components';
import { LogService } from '@minikaenguru-ws/common-logging';


@Injectable({
	providedIn: 'root'
})
export class KinderService {


	constructor(private http: HttpClient,
		private logger: LogService) { }


	public loadKinder(teilnahmenummer: string): Observable<Kind[]> {

		const url = environment.apiUrl + '/kinder/' + teilnahmenummer;

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public checkDuplikat(data: KindRequestData): Observable<Duplikatwarnung> {

		const url = environment.apiUrl + '/kinder/duplikate';

		this.logger.debug(JSON.stringify(data));


		return this.http.post(url, data, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public insertKind(data: KindRequestData): Observable<ResponsePayload> {

		const url = environment.apiUrl + '/kinder';

		return this.http.post(url, data, { observe: 'body' }).pipe(
			map(body => body as ResponsePayload)
		);

	}

	public updateKind(data: KindRequestData): Observable<ResponsePayload> {

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
