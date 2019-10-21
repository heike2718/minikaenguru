import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { ResponsePayload, JWTService } from 'hewi-ng-lib';
import { HttpClient } from '@angular/common/http';
import { STORAGE_KEY_CLIENT_ACCESS_TOKEN } from '../client/model/client.model';

@Injectable()
export class AuthService {

	private baseUri = environment.apiUrl;

	constructor(private http: HttpClient, private jwtService: JWTService) { }

	getUserProfile(): Observable<ResponsePayload> {

		return this.http.get<ResponsePayload>(this.baseUri + '/users/user/the-id');

	}

	login() {
		const accessToken = localStorage.getItem(STORAGE_KEY_CLIENT_ACCESS_TOKEN);
		if (accessToken) {
			const authUrl = this.jwtService.getLoginUrl(environment.authUrl, accessToken, environment.loginRedirectUrl, 'login', null);
			window.location.href = authUrl;
		}
	}
}
