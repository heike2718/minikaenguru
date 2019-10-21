import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from './reducers';
import { LogService, JWTService, AuthResult, STORAGE_KEY_JWT } from 'hewi-ng-lib';
import { Client, STORAGE_KEY_CLIENT, JWTPayload } from './client/model/client.model';
import { ClientService } from './client/client.service';
import { noop } from 'rxjs';
import { tap } from 'rxjs/operators';
import { HttpErrorHandler } from './error/http-error-handler.service';
import { initAccessToken, initJWT, deleteJWT } from './client/client.actions';
import { clientAccessTokenExpired, jwtExprired } from './client/client.utils';

@Component({
	selector: 'mkadm-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

	title = 'mkadmin-app';

	constructor(private store: Store<AppState>
		, private logger: LogService
		, private jwtService: JWTService
		, private clientService: ClientService
		, private errorHandler: HttpErrorHandler) { }

	ngOnInit(): void {

		const clientVal = localStorage.getItem(STORAGE_KEY_CLIENT);

		if (clientVal) {
			const client: Client = JSON.parse(clientVal);
			if (!clientAccessTokenExpired(client.expiresAt)) {
				this.store.dispatch(initAccessToken({ client }));
			} else {
				this.orderClientAccessToken();
			}
		} else {
			this.orderClientAccessToken();
		}

		const hash = window.location.hash;

		if (hash && hash.indexOf('idToken') > 0) {

			this.logger.debug('[App Component] after redirect from authprovider');
			const authResult: AuthResult = this.jwtService.parseHash(hash);

			if (authResult.state && 'signup' !== authResult.state) {

				const jwt: JWTPayload = {
					jwt: authResult.idToken,
					expiresAtSeconds: authResult.expiresAt
				};

				this.store.dispatch(initJWT({ jwt }));
			}
		} else {
			this.logger.debug('[App Component] page refreshed, check the JWT');
			const jwtVal = localStorage.getItem(STORAGE_KEY_JWT);

			if (jwtVal) {
				const jwt: JWTPayload = JSON.parse(jwtVal);
				if (!jwtExprired(jwt.expiresAtSeconds)) {
					this.store.dispatch(initJWT({ jwt }));
				} else {
					this.store.dispatch(deleteJWT());
				}
			}
		}
	}

	private orderClientAccessToken() {
		this.clientService.orderClientAccessToken().pipe(
			tap(responsePayload => {
				const clientResponse: Client = responsePayload.data;
				this.store.dispatch(initAccessToken({ client: clientResponse }));
				this.logger.info('[App Component] clientAccessToken initialized', clientResponse.accessToken);
			})
		).subscribe(
			noop,
			error => this.errorHandler.handleError(error, 'order first client access token')
		);
	}

}
