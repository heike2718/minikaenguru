import { Injectable, Inject } from '@angular/core';
import { MkvAuthConfigService, MkvAuthConfig } from './configuration/mkv-auth-config';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { ResponsePayload, MessageService } from '@minikaenguru-ws/common-messages';
import { map } from 'rxjs/operators';
import { LogService } from '@minikaenguru-ws/common-logging';
import { AuthResult } from './domain/entities';
import { Observable } from 'rxjs';

@Injectable({
	providedIn: 'root'
})
export class AuthService {

	constructor(@Inject(MkvAuthConfigService) private config: MkvAuthConfig
		, private http: HttpClient
		, private messagesService: MessageService
		, private logger: LogService){}


	lehrerkontoAnlegen(schulkuerzel: string) {


		const url = this.config.baseUrl + '/mkv-app/authurls/signup/lehrer/' + schulkuerzel;

		this.http.get(url).pipe(
			map(body => body['data'] as ResponsePayload)
		).subscribe(
			payload => {
				window.location.href = payload.message.message;
			},
			(error => {
				this.handleError(error, '[AuthService] lehrerkontoAnlegen')
			}));

	}

	public createSession(authResult: AuthResult) {

		// hier vielleicht ein Observable zurückgeben und die Anwendung liest dann die Session ein.
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
					case 401:
					case 908:
						break;
					default: {
						this.logger.error(context + ' url=' + httpError.url);
						if (msg) {
							this.showServerResponseMessage(msg);
						} else {
								this.messagesService.error('Es ist ein unerwarteter Fehler aufgetreten. Bitte schreiben Sie eine Mail an minikaenguru@egladil.de');
						}
					}
				}
			}
		}
	}
	private extractMessageObject(error: HttpErrorResponse): { level: string, message: string } {

		if (error.error && error.error.message) {
			return { level: 'ERROR', message: error.error.message };
		}

		return null;
	}

	private showServerResponseMessage(msg: { level: string, message: string }) {
		switch (msg.level) {
			case 'WARN':
				this.messagesService.error(msg.message);
				break;
			case 'ERROR':
				this.messagesService.error(msg.message);
				break;
			default:
				this.messagesService.error('Unbekanntes message.level ' + msg.level + ' vom Server bekommen.');
		}
	}
}

