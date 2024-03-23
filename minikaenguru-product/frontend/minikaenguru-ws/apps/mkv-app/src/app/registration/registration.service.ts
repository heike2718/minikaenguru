import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResult } from '@minikaenguru-ws/common-auth';
import { environment } from '../../environments/environment';

import { Observable } from 'rxjs';
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';


@Injectable({
	providedIn: 'root'
})
export class RegistrationService {

	constructor(private http: HttpClient, private loadingIndicatorService: LoadingIndicatorService) { }

	public createVeranstalter(authResult: AuthResult): Observable<any> {

		const url = environment.apiUrl + '/veranstalter';
		window.location.hash = '';

		return this.loadingIndicatorService.showLoaderUntilCompleted(this.http.post(url, authResult));

	}

}
