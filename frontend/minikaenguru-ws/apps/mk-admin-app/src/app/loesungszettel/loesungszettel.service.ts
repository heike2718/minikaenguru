import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Loesungszettel } from './loesungszettel.model';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelService {

    constructor (private http: HttpClient){}

    public countLoesungszettel(wettbewerbsjahr: number): Observable<number> {

        const url = environment.apiUrl + '/loesungszettel/' + wettbewerbsjahr + '/size';

        return this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        );
    }


    public loadPage(wettbewerbsjahr: number, limit: number, offset: number): Observable<Loesungszettel[]> {

        const url = environment.apiUrl + '/loesungszettel/' + wettbewerbsjahr + '?limit=' + limit + '&offset=' + offset;

        return this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        );

    }
}