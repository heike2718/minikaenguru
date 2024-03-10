import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { Loesungszettel } from './loesungszettel.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
    providedIn: 'root'
})
export class LoesungszettelService {

    constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

    public countLoesungszettel(wettbewerbsjahr: number): Observable<number> {

        const url = environment.apiUrl + '/loesungszettel/' + wettbewerbsjahr + '/size';

        const obs$ = this.http.get(url).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data)
        );

        return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
    }


    public loadPage(wettbewerbsjahr: number, limit: number, offset: number): Observable<Loesungszettel[]> {

        const url = environment.apiUrl + '/loesungszettel/' + wettbewerbsjahr + '?limit=' + limit + '&offset=' + offset;

        const obs$ = this.http.get(url).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data)
        );

        return this.loadingIndicatorService.showLoaderUntilCompleted(obs$);
    }
}