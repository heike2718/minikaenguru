import * as moment_ from 'moment';
import { Injectable, Inject } from '@angular/core';
import { MkAuthConfigService, MkAuthConfig } from './configuration/mk-auth-config';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Store } from '@ngrx/store';
import { ResponsePayload, MessageService } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { LogService } from '@minikaenguru-ws/common-logging';
import { AuthResult, STORAGE_KEY_DEV_SESSION_ID, STORAGE_KEY_SESSION_EXPIRES_AT, STORAGE_KEY_USER, Session } from './domain/entities';
import { AuthState } from './+state/auth.reducer';
import { login, logout, refreshSession, startLoggingOut } from './+state/auth.actions';
import { Router } from '@angular/router';
import { user, isLoggedIn, isLoggedOut, onLoggingOut } from './+state/auth.selectors';
const moment = moment_;

@Injectable({
	providedIn: 'root'
})
export class AuthService {

	public user$ = this.store.select(user);
	isLoggedIn$ = this.store.select(isLoggedIn);
	isLoggedOut$ = this.store.select(isLoggedOut);
	onLoggingOut$ = this.store.select(onLoggingOut);

	sessionUrl: string;

	constructor(@Inject(MkAuthConfigService) private config: MkAuthConfig
		, private http: HttpClient
		, private store: Store<AuthState>
		, private router: Router
		, private messagesService: MessageService
		, private logger: LogService) {

			this.sessionUrl = this.config.baseUrl + '/session';
		}


	public lehrerkontoAnlegen(schulkuerzel: string, newsletterAbo: boolean) {


		const url = this.sessionUrl + '/authurls/signup/lehrer/' + schulkuerzel + '/' + newsletterAbo;

		this.http.get(url).pipe(
			map(body => body as ResponsePayload)
		).subscribe(
			payload => {
				window.location.href = payload.message.message;
			},
			(error => {
				this.handleError(error, '[AuthService] lehrerkontoAnlegen')
			}));

	}

	public privatkontoAnlegen(newsletterAbo: boolean) {
		const url = this.sessionUrl + '/authurls/signup/privat/' + newsletterAbo;

		this.http.get(url).pipe(
			map(body => body as ResponsePayload)
		).subscribe(
			payload => {
				window.location.href = payload.message.message;
			},
			(error => {
				this.handleError(error, '[AuthService] privatkontoAnlegen')
			}));
	}

	public login() {
		const url = this.sessionUrl + '/authurls/login';

		this.http.get(url).pipe(
			map(body => body as ResponsePayload)
		).subscribe(
			payload => {
				window.location.href = payload.message.message;
			},
			(error => {
				this.handleError(error, '[AuthService] login')
			}));
	}


	public createSession(authResult: AuthResult) {

		const url = this.sessionUrl + '/login';

		window.location.hash = '';

		this.http.post(url, authResult).pipe(
			map(body => body as ResponsePayload)
		).subscribe(
			payload => {
				// der auth.effect schiebt die Daten anschließend in den localStorage
				this.store.dispatch(login({ session: payload.data }));
				this.router.navigateByUrl(this.config.loginSuccessUrl);
			},
			(error => {
				this.handleError(error, '[AuthService] createSession')
			}));
	}

	logOut(redirectToProfileApp: boolean): void {

		this.store.dispatch(startLoggingOut());

		const devSessionId = localStorage.getItem(this.config.storagePrefix + STORAGE_KEY_DEV_SESSION_ID);
		let url = this.sessionUrl + '/logout';

		if (devSessionId) {
			url = this.sessionUrl + '/dev/logout/' + devSessionId;
		}

		this.http.delete(url).pipe(
			map(body => body as ResponsePayload)
		).subscribe(
			_payload => {
				// der auth.effect löscht die Daten anschließend aus dem localStorage (oder auch nicht :/)
				this.store.dispatch(logout());
				if (redirectToProfileApp) {
					window.location.href = this.config.profileUrl;
				} else {
					this.router.navigateByUrl('/');
				}
			},
			(error => {
				this.handleError(error, '[AuthService] logout')
			}));
	}

	public parseHash(hashStr: string): AuthResult {

		hashStr = hashStr.replace(/^#?\/?/, '');

		const result: AuthResult = {
			expiresAt: 0,
			nonce: undefined,
			state: undefined,
			idToken: undefined
		};

		if (hashStr.length > 0) {

			const tokens = hashStr.split('&');
			tokens.forEach(
				(token) => {
					const keyVal = token.split('=');
					switch (keyVal[0]) {
						case 'expiresAt': result.expiresAt = JSON.parse(keyVal[1]); break;
						case 'nonce': result.nonce = keyVal[1]; break;
						case 'state': result.state = keyVal[1]; break;
						case 'idToken': result.idToken = keyVal[1]; break;
					}
				}
			);
		}
		return result;
	}

	handleError(error: any, context: string) {

		if (error instanceof ErrorEvent) {
			this.logger.error(context + ': ErrorEvent occured - ' + JSON.stringify(error));
			throw (error);
		} else {

			const httpError = error as HttpErrorResponse;

			if (httpError.status === 0) {
				this.messagesService.error('Der Server ist nicht erreichbar.');
			} else {
				const msg = this.extractMessageObject(httpError);
				switch (httpError.status) {
					default: {
						this.logger.error(context + ' url=' + httpError.url);
						if (msg) {
							this.showServerResponseMessage(msg.level, msg.message);
						} else {
							this.messagesService.error('Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de');
						}
					}
				}
			}
		}
	}


	private extractMessageObject(error: HttpErrorResponse): { level: string, message: string } | null {

		if (error.error && error.error.message) {
			return { level: 'ERROR', message: error.error.message['message'] };
		}

		return null;
	}

	private showServerResponseMessage(level: string, message: string) {

		switch (level) {
			case 'WARN':
				this.messagesService.error(message);
				break;
			case 'ERROR':
				this.messagesService.error(message);
				break;
			default:
				this.messagesService.error('Unbekanntes message.level ' + level + ' vom Server bekommen.');
		}
	}

	public clearOrRestoreSession() {

		if (this.sessionExpired()) {
			this.clearSession();
		} else {
			this.initSessionFromStorage();
		}
	}

	private sessionExpired(): boolean {

		this.logger.debug('check session');

		// session expires at ist in Millisekunden seit 01.01.1970
		const expiration = this.getExpirationAsMoment();
		if (expiration === null) {
			return true;
		}
		const expired = moment().isAfter(expiration);

		return expired;
	}



	private clearSession() {

		this.store.dispatch(logout());
	}

	private initSessionFromStorage() {

		const expiration = localStorage.getItem(this.config.storagePrefix + STORAGE_KEY_SESSION_EXPIRES_AT);
		const user = localStorage.getItem(this.config.storagePrefix + STORAGE_KEY_USER);
		if (expiration) {

			const userObject = user ? JSON.parse(user): null;

			const session: Session = {
				expiresAt: JSON.parse(expiration),
				sessionId: localStorage.getItem(this.config.storagePrefix + STORAGE_KEY_DEV_SESSION_ID),
				user: userObject
			};

			this.store.dispatch(refreshSession({ session: session }));
		}
	}

	private getExpirationAsMoment() {

		const expiration = localStorage.getItem(this.config.storagePrefix + STORAGE_KEY_SESSION_EXPIRES_AT);
		if (!expiration || expiration === '0') {
			console.log('no session present');
			this.logger.debug('no session present');
			return null;
		}

		console.log('session present');
		const expiresAt = JSON.parse(expiration);
		return moment(expiresAt);
	}
}

