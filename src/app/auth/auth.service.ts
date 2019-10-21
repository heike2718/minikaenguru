import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { ResponsePayload } from 'hewi-ng-lib';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class AuthService {

	private baseUri = environment.apiUrl;

	constructor(private http: HttpClient) { }

	login(): Observable<ResponsePayload> {

		return this.http.get<ResponsePayload>(this.baseUri + '/users/user/the-id');

	}
}
