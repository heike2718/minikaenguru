import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs/internal/Observable';
import { ResponsePayload, JWTService, LogService } from 'hewi-ng-lib';
import { HttpClient } from '@angular/common/http';
import { STORAGE_KEY_CLIENT, Client } from '../client/model/client.model';
import { ClientService } from '../client/client.service';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { HttpErrorHandler } from '../error/http-error-handler.service';
import { noop } from 'rxjs';
import { tap } from 'rxjs/operators';
import { initAccessToken } from '../client/client.actions';

@Injectable()
export class AuthService {

	private baseUri = environment.apiUrl;

	constructor(private http: HttpClient
		, private jwtService: JWTService
		, private clientService: ClientService
		, private store: Store<AppState>
		, private errorHandler: HttpErrorHandler
		, private logger: LogService) { }

	getUserProfile(): Observable<ResponsePayload> {

		return this.http.get<ResponsePayload>(this.baseUri + '/users/user/the-id');

	}

	login() {
		const clientVal = localStorage.getItem(STORAGE_KEY_CLIENT);
		if (clientVal) {

			const client: Client = JSON.parse(clientVal);


			const authUrl = this.jwtService.getLoginUrl(environment.authUrl, client.accessToken, environment.loginRedirectUrl, 'login', null);
			window.location.href = authUrl;
		} else {
			this.clientService.orderClientAccessToken().pipe(tap(responsePayload => {
				const clientResponse: Client = responsePayload.data;
				this.store.dispatch(initAccessToken({ client: clientResponse }));
				this.logger.info('[App Component] clientAccessToken initialized', clientResponse.accessToken);

				// tslint:disable-next-line: max-line-length
				const authUrl = this.jwtService.getLoginUrl(environment.authUrl, clientResponse.accessToken, environment.loginRedirectUrl, 'login', null);
				window.location.href = authUrl;
			})
			).subscribe(
				noop,
				error => this.errorHandler.handleError(error, 'order first client access token')
			);
		}
	}
}
