import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';


@Injectable({
	providedIn: 'root'
})
export class DownloadService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }


	public downloadFile(url: string): Observable<HttpResponse<any>> {

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.get(url, { observe: 'response', responseType: 'blob' }));

	}
}
