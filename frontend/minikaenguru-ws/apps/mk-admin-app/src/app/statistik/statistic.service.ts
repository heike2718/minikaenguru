import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { StatistikEntity, StatistikGruppeninfo } from './statistik.model';


@Injectable({
	providedIn: 'root'
})
export class StatistikService {


    constructor(private http: HttpClient) {}

    public loadStatistik(statistikEntity: StatistikEntity) : Observable<StatistikGruppeninfo> {


        const url = environment.apiUrl + '/statistik/' + statistikEntity.toLocaleLowerCase();

		return this.http.get(url).pipe(
			map(body => body as ResponsePayload),
			map(payload => payload.data)
		);
    }
}