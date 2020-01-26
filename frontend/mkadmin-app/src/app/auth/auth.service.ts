import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { ResponsePayload, LogService, AuthResult } from 'hewi-ng-lib';
import { HttpClient } from '@angular/common/http';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { HttpErrorHandler } from '../error/http-error-handler.service';
import { map, tap } from 'rxjs/operators';
import { User, STORAGE_KEY_USER } from './model/user.model';
import { loadUser, logOut } from './auth.actions';
import { noop } from 'rxjs';

@Injectable()
export class AuthService {

	private baseUri = environment.apiUrl;

	constructor(private httpClient: HttpClient
		, private store: Store<AppState>
		, private errorHandler: HttpErrorHandler
		, private logger: LogService) { }

	getUserProfile(): Observable<ResponsePayload> {

		return this.httpClient.get<ResponsePayload>(this.baseUri + '/users/user/the-id');

	}

	createSession(authResult: AuthResult) {

		window.location.hash = '';

		const url = environment.apiUrl + '/auth/session';

		this.httpClient.post(url, authResult.idToken).pipe(
			map(res => res as ResponsePayload)
		).subscribe(
			payload => {
				if (payload.data) {
					const user = payload.data as User;
					this.logger.debug('createSession: user=' + JSON.stringify(user));
					// der auth.effect sorgt dafür, dass der user in den localStorage geschrieben wird.
					this.store.dispatch(loadUser({ user }));
				}
			},
			(error => {
				this.errorHandler.handleError(error, 'createSession');
			})
		);


	}

	logIn() {

		const url = environment.apiUrl + '/auth/login';

		this.httpClient.get(url).pipe(
			map(res => res as ResponsePayload)
		).subscribe(
			payload => {
				window.location.href = payload.message.message;
			},
			(error => {
				this.errorHandler.handleError(error, 'logIn');
			}));

	}

	logOut() {
		let url = environment.apiUrl;

		const userVal = localStorage.getItem(STORAGE_KEY_USER);

		if (userVal) {

			const user = JSON.parse(userVal) as User;
			const sessionId = user.sessionId;


			if (sessionId) {
				url = url + '/auth/dev/logout/' + sessionId;
			} else {
				url = url + '/auth/dev/logout/unknown';
			}

			if (environment.production) {
				url = environment.apiUrl + '/auth/logout';
			}

			this.logger.debug('url=' + url);

			this.httpClient.delete(url).pipe(
				tap(_res => {
					// auth.effect sorgt dafür, dass user aus dem local storage gelöscht wird und man zu home gerouted wird
					this.store.dispatch(logOut());
				})
			).subscribe(
				noop,
				(_error => {
					// ist nicht schlimm: die session bleibt auf dem Server
					this.store.dispatch(logOut());
				}));
		}
	}
}

