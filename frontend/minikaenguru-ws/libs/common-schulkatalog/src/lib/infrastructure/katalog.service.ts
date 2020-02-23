import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SchulkatalogConfigService, SchulkatalogConfig } from '../configuration/schulkatalog-config';
import { Katalogtyp, InverseKatalogItem } from '../domain/entities';
import { Observable, of } from 'rxjs';
import { map, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { SchulkatalogState } from '../+state/schulkatalog.reducer';
import { startSearch } from '../+state/schulkatalog.actions';

@Injectable({
  providedIn: 'root'
})
export class KatalogService {

  private baseUrl: string;
  private queryParamPrefix = '?search=';

  constructor(@Inject(SchulkatalogConfigService) private config, private http: HttpClient, private store: Store<SchulkatalogState> ) {

    this.baseUrl = config.baseUrl;

    console.log('[KatalogService] ' + this.baseUrl);
  }

  public searchKatalogItems(typ: Katalogtyp, terms: Observable<string>): Observable<InverseKatalogItem[]> {

    let url = '';
    switch(typ) {
	  case 'LAND':
		url = this.baseUrl + '/kataloge/laender';
		break;
      case 'ORT':
        url = this.baseUrl + '/kataloge/orte';
        break;
      case 'SCHULE':
        url = this.baseUrl + '/kataloge/schulen';
        break;
    default:
        throw 'unsupported Katalogtyp ' + typ;
    }

    return terms.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      switchMap(term => this.searchEntries(typ, url, term))
    );
  }

  private searchEntries(typ: Katalogtyp, url: string, term: string): Observable<InverseKatalogItem[]> {

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
          map( body => body['data'] as  InverseKatalogItem[])
        );
  }
}
















