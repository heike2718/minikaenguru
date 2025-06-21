import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SchulkatalogConfigService, SchulkatalogConfig } from '../configuration/schulkatalog-config';
import { Katalogtyp, KatalogItem, SchulkatalogAntrag } from '../domain/entities';
import { Observable, of } from 'rxjs';
import { map } from 'rxjs/operators';
import { LogService } from '@minikaenguru-ws/common-logging';
import { ResponsePayload, Message } from '@minikaenguru-ws/common-messages';

@Injectable({
	providedIn: 'root'
})
export class KatalogService {

	private queryParamPrefix = '?search=';

	constructor(@Inject(SchulkatalogConfigService) private config: SchulkatalogConfig
		, private http: HttpClient
		, private logger: LogService) { }

	public searchKatalogItems(typ: Katalogtyp, serachTerm: string): Observable<KatalogItem[]> {

		let url = '';
		switch (typ) {
			case 'LAND':
				url = this.config.baseUrl + '/schulkatalog/suche/land';
				break;
			case 'ORT':
				url = this.config.baseUrl + '/schulkatalog/suche/ort';
				break;
			case 'SCHULE':
				url = this.config.baseUrl + '/schulkatalog/suche/schule';
				break;
			default:
				throw new Error('unsupported Katalogtyp ' + typ);
		}

		return this.searchEntries(url, serachTerm);
	}

	public searchKindelemente(katalogItem: KatalogItem, serachTerm: string): Observable<KatalogItem[]> {

		let url = '';
		switch (katalogItem.typ) {
			case 'LAND':
				url = this.config.baseUrl + '/schulkatalog/suche/laender/' + katalogItem.kuerzel + '/orte';
				break;
			case 'ORT':
				url = this.config.baseUrl + '/schulkatalog/suche/orte/' + katalogItem.kuerzel + '/schulen';
				break;
			case 'SCHULE':
				break;
			default:
				throw new Error('unsupported Katalogtyp ' + katalogItem.typ);
		}

		return this.searchEntries(url, serachTerm);
	}

	public loadKindelemente(katalogItem: KatalogItem): Observable<KatalogItem[]> {

		if (katalogItem.anzahlKinder > this.config.immediatelyLoadOnNumberChilds || katalogItem.leaf) {

			return of([]);
		}

		let url = '';
		switch (katalogItem.typ) {
			case 'LAND':
				url = this.config.baseUrl + '/schulkatalog/laender/' + katalogItem.kuerzel + '/orte';
				break;
			case 'ORT':
				url = this.config.baseUrl + '/schulkatalog/orte/' + katalogItem.kuerzel + '/schulen';
				break;
			default:
				url = '';
				this.logger.warn('KatalogService.loadKindelemente() mit unerwartetem KatalogTyp ' + katalogItem.typ + ' aufgerufen');
		}

		if (url.length > 0) {

			return this.http
				.get(url).pipe(
					map(body => body as ResponsePayload),
					map(rp => rp.data as KatalogItem[])
				);
		}

		return of([]);
	}

	private searchEntries(url: string, term: string): Observable<KatalogItem[]> {

		if (term.length === 0) {
			return of([]);
		}

		const finalUrl = url + this.queryParamPrefix + term;

		return this.http
			.get(finalUrl).pipe(
				map(body => body as ResponsePayload),
				map(rp => rp.data as KatalogItem[])
			);
	}

	public submitSchulkatalogAntrag(antrag: SchulkatalogAntrag): Observable<Message> {

		const url = this.config.baseUrl + '/schulkatalog/katalogantrag';

		return this.http.post(url, antrag).pipe(
			map(body => body as ResponsePayload),
			map(rp => rp.message)
		);
	}
}

