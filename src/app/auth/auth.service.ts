import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { ResponsePayload, JWTService } from 'hewi-ng-lib';
import { HttpClient } from '@angular/common/http';
import { STORAGE_KEY_CLIENT, Client } from '../client/model/client.model';

@Injectable()
export class AuthService {

	private baseUri = environment.apiUrl;

	constructor(private http: HttpClient, private jwtService: JWTService) { }

	getUserProfile(): Observable<ResponsePayload> {

		return this.http.get<ResponsePayload>(this.baseUri + '/users/user/the-id');

	}

	login() {
		const clientVal = localStorage.getItem(STORAGE_KEY_CLIENT);
		if (clientVal) {

			const client: Client = JSON.parse(clientVal);


			const authUrl = this.jwtService.getLoginUrl(environment.authUrl, client.accessToken, environment.loginRedirectUrl, 'login', null);
			window.location.href = authUrl;
		}
	}
}
