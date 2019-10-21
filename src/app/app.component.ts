import { Component, OnInit } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { AppState } from './reducers';
import { login } from './auth/auth-actions';
import { LogService, JWTService, STORAGE_KEY_JWT_STATE } from 'hewi-ng-lib';
import { AuthService } from './auth/auth.service';
import { STORAGE_KEY_CLIENT_ACCESS_TOKEN, Client } from './client/model/client.model';
import { ClientService } from './client/client.service';
import { noop, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { HttpErrorHandler } from './error/http-error-handler.service';
import { initAccessToken } from './client/client.actions';
import { isClientTokenValid } from './client/client.selectors';

@Component({
	selector: 'mkadm-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

	title = 'mkadmin-app';

	private clientTokenValid: boolean;

	constructor(private store: Store<AppState>
		, private logger: LogService
		, private jwtService: JWTService
		, private authService: AuthService
		, private clientService: ClientService
		, private errorHandler: HttpErrorHandler) { }

	ngOnInit(): void {

		this.store.pipe(
			select(isClientTokenValid)
		).subscribe(
			() => this.clientTokenValid
		);

		if (!this.clientTokenValid) {
			this.clientService.orderClientAccessToken().pipe(
				tap(responsePayload => {
					const client: Client = responsePayload.data;
					this.store.dispatch(initAccessToken({ client }));

					this.logger.info('clientAccessToken initialized', client.accessToken);
				})
			).subscribe(
				noop,
				error => this.errorHandler.handleError(error, 'order first client access token')
			);
		}

		// const clientAccessToken = localStorage.getItem(STORAGE_KEY_CLIENT_ACCESS_TOKEN);
		// const hash = window.location.hash;
		// if (hash && hash.indexOf('idToken') > 0) {
		// 	this.logger.debug('after redirect from authprovider', clientAccessToken);
		// } else {
		// 	if (!clientAccessToken) {
		// 		this.clientService.orderClientAccessToken().pipe(
		// 			tap(responsePayload => {
		// 				this.store.dispatch(initAccessToken({ client: responsePayload.data }));
		// 			})
		// 		).subscribe(
		// 			noop,
		// 			error => this.errorHandler.handleError(error, 'order first client access token')
		// 		);
		// 	}
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


	private isLoggedIn(): boolean {
		const authState = localStorage.getItem(STORAGE_KEY_JWT_STATE);
		if (authState && 'signup' === authState) {
			return false;
		}
		return !this.jwtService.isJWTExpired();
	}

}
