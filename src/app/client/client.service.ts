import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ResponsePayload } from 'hewi-ng-lib';
import { STORAGE_KEY_CLIENT, Client } from './model/client.model';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';

@Injectable({
	providedIn: 'root'
})
export class ClientService {

	constructor(private http: HttpClient) { }


	orderClientAccessToken(): Observable<ResponsePayload> {

		const clientVal = localStorage.getItem(STORAGE_KEY_CLIENT);


		let url = environment.apiUrl + '/accesstoken/';

		if (clientVal) {
			const client: Client = JSON.parse(clientVal);

			url += client.accessToken;
		} else {
			url += 'initial';
		}

		return this.http.get<ResponsePayload>(url);
	}
}

