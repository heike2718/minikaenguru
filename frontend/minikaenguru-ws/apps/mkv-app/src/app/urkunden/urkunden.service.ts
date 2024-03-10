import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { UrkundenauftragEinzelkind, UrkundenauftragSchule } from './urkunden.model';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';
@Injectable({
	providedIn: 'root'
})
export class UrkundenService {


	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }


	public generateUrkunde(auftrag: UrkundenauftragEinzelkind): Observable<HttpResponse<any>> {

		const url = environment.apiUrl + '/urkunden/urkunde';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post(url, auftrag, { observe: 'response', responseType: 'blob' }));
	}


	public generateSchulauswertung(auftrag: UrkundenauftragSchule): Observable<HttpResponse<any>> {

		const url = environment.apiUrl + '/urkunden/schule';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post(url, auftrag, { observe: 'response', responseType: 'blob' }));
	}


}
