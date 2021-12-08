import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { UploadMonitoringInfo } from './uploads.model';

@Injectable({
	providedIn: 'root'
})
export class UploadsService {

    constructor (private http: HttpClient){}

    public countUploads(): Observable<number> {

        const url = environment.apiUrl + '/uploads/size';

        return this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        );
    }

    public loadUploadsKlassenlistenByTeilnahmenummer(teilnahmenummer: string): Observable<UploadMonitoringInfo[]> {

        const url = environment.apiUrl + '/schulen/' + teilnahmenummer + '/uploads/klassenlisten';

        return this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        );
    }

    public loadPage(limit: number, offset: number): Observable<UploadMonitoringInfo[]> {

        const url = environment.apiUrl + '/uploads?limit=' + limit + '&offset=' + offset;

        return this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        );

    }

    
}