import { Component, OnInit } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { AppState } from './reducers';
import { LogService, JWTService, STORAGE_KEY_JWT_STATE, AuthResult } from 'hewi-ng-lib';
import { AuthService } from './auth/auth.service';
import { Client, STORAGE_KEY_CLIENT, JWTPayload } from './client/model/client.model';
import { ClientService } from './client/client.service';
import { noop } from 'rxjs';
import { tap } from 'rxjs/operators';
import { HttpErrorHandler } from './error/http-error-handler.service';
import { initAccessToken, initJWT } from './client/client.actions';
import { clientAccessTokenExpired } from './client/client.utils';
import { isLoggedIn } from './client/client.selectors';
import { userLoaded } from './auth/auth.selectors';

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
				this.clientService.orderClientAccessToken().pipe(
					tap(responsePayload => {
						const clientResponse: Client = responsePayload.data;
						this.store.dispatch(initAccessToken({ client: clientResponse }));
						this.logger.info('clientAccessToken initialized', clientResponse.accessToken);
					})
				).subscribe(
					noop,
					error => this.errorHandler.handleError(error, 'order first client access token')
				);
			}
		} else {
			this.clientService.orderClientAccessToken().pipe(
				tap(responsePayload => {
					const clientResponse: Client = responsePayload.data;
					this.store.dispatch(initAccessToken({ client: clientResponse }));
					this.logger.info('clientAccessToken initialized', clientResponse.accessToken);
				})
			).subscribe(
				noop,
				error => this.errorHandler.handleError(error, 'order first client access token')
			);
		}

		const hash = window.location.hash;

		if (hash && hash.indexOf('idToken') > 0) {

			this.logger.debug('after redirect from authprovider');
			const authResult: AuthResult = this.jwtService.parseHash(hash);

			if (authResult.state && 'signup' !== authResult.state) {

				const jwt: JWTPayload = {
					jwt: authResult.idToken,
					expiresAtSeconds: authResult.expiresAt
				};

				this.store.dispatch(initJWT({ jwt }));
			}

			// TODO: hier muss noch der user geholt werden
			// 	if (this.isLoggedIn()) {
			// 		this.logger.debug('after redirect from authprovider', clientAccessToken);
			// 		this.authService.getUserProfile();
			// 	}
			// }

			// const userProfile = localStorage.getItem('user');

			// if (userProfile) {
			// 	this.store.dispatch(login({
			// 		user: JSON.parse(userProfile)
			// 	}));
			// }
		}
	}

}
