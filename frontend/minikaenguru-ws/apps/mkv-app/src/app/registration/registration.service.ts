import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResult } from '@minikaenguru-ws/common-auth';
import { environment } from '../../environments/environment';
import { ResponsePayload, MessageService } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { Store } from '@ngrx/store';
import { RegistrationState } from './+state/registration.reducer';

import * as RegistrationActions from './+state/registration.actions';


@Injectable({
	providedIn: 'root'
})
export class RegistrationService {

	constructor(private http: HttpClient
		, private store: Store<RegistrationState>
		, private messageService: MessageService) { }

	public createUser(authResult: AuthResult) {

		const url = environment.apiUrl + '/mkv-app/users';


		this.http.post(url, authResult).pipe(
			map(body => body['data'] as ResponsePayload)
		).subscribe(
			payload => {
				this.store.dispatch(RegistrationActions.userCreated({ message: payload.message.message }));
			},
			(_error => {
				// TODO this.handleError(error, '[AuthService] createUser')
				this.messageService.error('Beim Anlegen eines Benutzerkontos ist etwas schiefgegangen')
			}));

	}

}
