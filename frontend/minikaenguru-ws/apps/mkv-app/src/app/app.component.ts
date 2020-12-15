import { Component, TemplateRef, ViewChild } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthService, AuthResult } from '@minikaenguru-ws/common-auth';
import { RegistrationService } from './registration/registration.service';
import { map } from 'rxjs/operators';
import { ResponsePayload, MessageService } from '@minikaenguru-ws/common-messages';
import { RouterOutlet } from '@angular/router';

@Component({
	selector: 'mkv-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {



	envName = environment.envName;
	showEnv = environment.envName === 'DEV';
	api = environment.apiUrl;
	version = environment.version;

	constructor(private authService: AuthService
		, private registrationService: RegistrationService
		, private messageService: MessageService) {


		this.authService.clearOrRestoreSession();

		const hash = window.location.hash;

		if (hash && hash.indexOf('idToken') > 0) {
			const authResult = this.authService.parseHash(hash) as AuthResult;

			if (authResult.state) {
				if (authResult.state === 'login') {
					this.authService.createSession(authResult);

				}
				if (authResult.state === 'signup') {
					this.registrationService.createVeranstalter(authResult).pipe(
						map(body => body as ResponsePayload)
					).subscribe(
						payload => {
							this.messageService.info(payload.message.message);
						},
						(_error => {
							// TODO this.handleError(error, '[AuthService] createUser')
							this.messageService.error('Beim Anlegen eines Benutzerkontos ist etwas schiefgegangen')
						}));
				}
			} else {
				window.location.hash = '';
			}
		}

	}
	getAnimationData(outlet: RouterOutlet) {
		return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
	}
}
