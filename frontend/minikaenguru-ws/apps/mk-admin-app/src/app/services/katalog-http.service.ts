import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';

import { KatalogpflegeItem, Katalogpflegetyp, KuerzelAPIModel, SchulePayload } from '../katalogpflege/katalogpflege.model';

@Injectable({
	providedIn: 'root'
})
export class KatalogHttpService {

	constructor(private http: HttpClient) { }

	public loadLaender(): Observable<KatalogpflegeItem[]> {

		const url = environment.apiUrl + '/wb-admin/kataloge/laender';

		return this.loadKatalogItems(url);
	}

	public loadChildItems(item: KatalogpflegeItem): Observable<KatalogpflegeItem[]> {


		let url = environment.apiUrl + '/wb-admin/kataloge/';

		switch (item.typ) {
			case 'LAND': url += 'laender/' + item.kuerzel + '/orte'; break;
			case 'ORT': url += 'orte/' + item.kuerzel + '/schulen'; break;
		}

		return this.loadKatalogItems(url);
	}

	private loadKatalogItems(url: string): Observable<KatalogpflegeItem[]> {

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public searchKatalogItems(typ: Katalogpflegetyp, searchTerm: string): Observable<KatalogpflegeItem[]> {

		let url = environment.apiUrl + '/wb-admin/katalogsuche/global/' + typ + '?search=' + searchTerm;
		return this.loadKatalogItems(url);
	}

	public getKuerzel(): Observable<KuerzelAPIModel> {


		let url = environment.apiUrl + '/wb-admin/kuerzel';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
	}

	public createSchule(schulePayload: SchulePayload): Observable<ResponsePayload> {

		let url = environment.apiUrl + '/wb-admin/kataloge/schulen';

		return this.http.post(url, schulePayload).pipe(
			map(body => body as ResponsePayload)
		);

	}

	public renameSchule(schulePayload: SchulePayload): Observable<ResponsePayload> {

		let url = environment.apiUrl + '/wb-admin/kataloge/schulen';

		return this.http.put(url, schulePayload).pipe(
			map(body => body as ResponsePayload)
		);

	}
}

