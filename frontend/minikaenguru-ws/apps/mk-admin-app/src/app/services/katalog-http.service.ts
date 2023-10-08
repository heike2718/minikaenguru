import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';

import { KatalogpflegeItem, Katalogpflegetyp, KuerzelAPIModel, SchulePayload, OrtPayload, LandPayload } from '../katalogpflege/katalogpflege.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class KatalogHttpService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public loadLaender(): Observable<KatalogpflegeItem[]> {

		const url = environment.apiUrl + '/kataloge/laender';

		return this.loadKatalogItems(url);
	}

	public loadChildItems(parent: KatalogpflegeItem): Observable<KatalogpflegeItem[]> {


		let url = environment.apiUrl + '/kataloge/';

		switch (parent.typ) {
			case 'LAND': url += 'laender/' + parent.kuerzel + '/orte'; break;
			case 'ORT': url += 'orte/' + parent.kuerzel + '/schulen'; break;
		}

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.loadKatalogItems(url));
	}

	private loadKatalogItems(url: string): Observable<KatalogpflegeItem[]> {

		const obs$ = this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public searchKatalogItems(typ: Katalogpflegetyp, searchTerm: string): Observable<KatalogpflegeItem[]> {

		let url = environment.apiUrl + '/kataloge/suche/global/' + typ + '?search=' + searchTerm;
		return this.loadingIndicatorService.showLoaderUntilCompleted(this.loadKatalogItems(url));
	}

	public getKuerzel(): Observable<KuerzelAPIModel> {


		let url = environment.apiUrl + '/kataloge/kuerzel';

		const obs$ = this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public createSchule(schulePayload: SchulePayload): Observable<ResponsePayload> {

		let url = environment.apiUrl + '/kataloge/schulen';

		const obs$ = this.http.post(url, schulePayload).pipe(
			map(body => body as ResponsePayload)
		);

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);

	}

	public renameSchule(schulePayload: SchulePayload): Observable<ResponsePayload> {

		let url = environment.apiUrl + '/kataloge/schulen';

		const obs$ = this.http.put(url, schulePayload).pipe(
			map(body => body as ResponsePayload)
		); 

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);

	}

	public renameOrt(ortPayload: OrtPayload): Observable<ResponsePayload> {

		let url = environment.apiUrl + '/kataloge/orte';

		const obs$ = this.http.put(url, ortPayload).pipe(
			map(body => body as ResponsePayload)
		); 

		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}

	public renameLand(landPayload: LandPayload): Observable<ResponsePayload> {

		let url = environment.apiUrl + '/kataloge/laender';

		const obs$ = this.http.put(url, landPayload).pipe(
			map(body => body as ResponsePayload)
		);
		return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
	}
}

