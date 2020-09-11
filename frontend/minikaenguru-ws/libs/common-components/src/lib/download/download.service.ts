import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';


@Injectable({
	providedIn: 'root'
})
export class DownloadService {

	constructor(private http: HttpClient) { }


	public downloadFile(url: string): Observable<HttpResponse<any>> {

		return this.http.get(url, { observe: 'response', responseType: 'blob' });

	}



}
