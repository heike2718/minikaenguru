import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { StatistikGruppeninfo } from './statistik.model';


@Injectable({
	providedIn: 'root'
})
export class StatistikService {


    constructor(private http: HttpClient) {}

    public loadStatistikKinder() : Observable<StatistikGruppeninfo> {


        const url = environment.apiUrl + '/statistik/kinder';

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
    }
}