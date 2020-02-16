import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SchulkatalogConfigService, SchulkatalogConfig } from './configuration/schulkatalog-config';
import { Katalogtyp, InverseKatalogItem } from './domain/entities';
import { Observable, empty } from 'rxjs';
import { map, debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class KatalogService {

  private baseUrl: string;
  private queryParamPrefix = '?search=';

  constructor(@Inject(SchulkatalogConfigService) private config, private http: HttpClient ) {
    
    this.baseUrl = config.baseUrl;

    console.log('[KatalogService] ' + this.baseUrl);
  }

  public searchKatalogItems(typ: Katalogtyp, terms: Observable<string>): Observable<InverseKatalogItem[]> {

    let url = '';
    switch(typ) {
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
      return empty();
    }

    return this.http
        .get(url + this.queryParamPrefix + term).pipe(
          map( body => body['data'] as  InverseKatalogItem[])
        );
  }
}
