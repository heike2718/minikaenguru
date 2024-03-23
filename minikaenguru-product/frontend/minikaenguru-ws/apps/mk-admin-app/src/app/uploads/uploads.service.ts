import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';
import { UploadMonitoringInfo } from './uploads.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';

@Injectable({
	providedIn: 'root'
})
export class UploadsService {

    constructor (private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService){}

    public countUploads(): Observable<number> {

        const url = environment.apiUrl + '/uploads/size';

        return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        ));
    }

    public loadUploadsKlassenlistenByTeilnahmenummer(teilnahmenummer: string): Observable<UploadMonitoringInfo[]> {

        const url = environment.apiUrl + '/schulen/' + teilnahmenummer + '/uploads/klassenlisten';

        return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        ));
    }

    public loadPage(limit: number, offset: number): Observable<UploadMonitoringInfo[]> {

        const url = environment.apiUrl + '/uploads?limit=' + limit + '&offset=' + offset;

        return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url).pipe(
            map(body => body as ResponsePayload),
			map(payload => payload.data)
        ));

    }   
}