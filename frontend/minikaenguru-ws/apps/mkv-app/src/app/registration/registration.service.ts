import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResult } from '@minikaenguru-ws/common-auth';
import { environment } from '../../environments/environment';

import { Observable } from 'rxjs';


@Injectable({
	providedIn: 'root'
})
export class RegistrationService {

	constructor(private http: HttpClient) { }

	public createUser(authResult: AuthResult): Observable<any> {

		const url = environment.apiUrl + '/mkv-app/users';
		window.location.hash = '';

		return this.http.post(url, authResult);

	}

}
