import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { LogService } from 'hewi-ng-lib';

@Injectable()
export class DownloadService {

	constructor(private http: HttpClient, private logger: LogService) { }


	downloadCsv(): Observable<HttpResponse<string>> {

		let headers = new HttpHeaders();
		headers = headers.append('Accept', 'text/csv; charset=utf-8');

		const url = environment.apiUrl + '/files/csv';

		this.logger.info('Setzen gleich GET nach ' + url + ' ab');

		return this.http.get(url, {
			headers,
			observe: 'response',
			responseType: 'text'
		});
  }


  downloadPdf(): Observable<HttpResponse<Blob>> {

		let headers = new HttpHeaders();
		headers = headers.append('Accept', 'application/pdf');

		const url = environment.apiUrl + '/files/pdf';

		this.logger.info('Setzen gleich GET nach ' + url + ' ab');

		return this.http.get(url, {
			headers,
			observe: 'response',
			responseType: 'blob'
		});
	}

  downloadExcel(): Observable<HttpResponse<Blob>> {

		let headers = new HttpHeaders();
		headers = headers.append('Accept', 'application/vnd.ms-excel');

		const url = environment.apiUrl + '/files/excel';

		this.logger.info('Setzen gleich GET nach ' + url + ' ab');

		return this.http.get(url, {
			headers,
			observe: 'response',
			responseType: 'blob'
		});
	}

}
