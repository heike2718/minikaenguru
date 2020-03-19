import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SchulkatalogConfigService, SchulkatalogConfig } from '../configuration/schulkatalog-config';
import { Katalogtyp, KatalogItem } from '../domain/entities';
import { Observable, of } from 'rxjs';
import { map, debounceTime, distinctUntilChanged, switchMap, finalize, flatMap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { startSearch } from '../+state/schulkatalog.actions';

@Injectable({
	providedIn: 'root'
})
export class KatalogService {

	private queryParamPrefix = '?search=';

	constructor(@Inject(SchulkatalogConfigService) private config: SchulkatalogConfig, private http: HttpClient, private store: Store<SchulkatalogState>) { }

	public searchKatalogItemsNeu(typ: Katalogtyp, serachTerm: string): Observable<KatalogItem[]> {

		let url = '';
		switch (typ) {
			case 'LAND':
				url = this.config.baseUrl + '/katalogsuche/global/land';
				break;
			case 'ORT':
				url = this.config.baseUrl + '/katalogsuche/global/ort';
				break;
			case 'SCHULE':
				url = this.config.baseUrl + '/katalogsuche/global/schule';
				break;
			default:
				throw new Error('unsupported Katalogtyp ' + typ);
		}

		return this.searchEntries(typ, url, serachTerm);
	}

	private searchEntries(typ: Katalogtyp, url: string, term: string): Observable<KatalogItem[]> {

		if (term.length === 0 && typ !== 'LAND') {
			return of([]);
		}

		this.store.dispatch(startSearch());

		let finalUrl = url;

		if (typ !== 'LAND') {
			finalUrl += this.queryParamPrefix + term;
		}

		return this.http
			.get(finalUrl).pipe(
				map(body => body['data'] as KatalogItem[])
			);
	}
}
















