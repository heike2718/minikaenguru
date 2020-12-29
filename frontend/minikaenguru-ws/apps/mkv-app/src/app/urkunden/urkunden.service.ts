import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable, of } from 'rxjs';
import { UrkundenauftragEinzelkind } from './urkunden.model';
@Injectable({
	providedIn: 'root'
})
export class UrkundenService {


	constructor(private http: HttpClient) { }


	public generateUrkunde(auftrag: UrkundenauftragEinzelkind): Observable<HttpResponse<any>> {

		const url = environment.apiUrl + '/veranstalter/urkunden/urkunde';

		return this.http.post(url, auftrag, { observe: 'response', responseType: 'blob' });
	}


}
