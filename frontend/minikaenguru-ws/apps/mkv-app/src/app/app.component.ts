import { Component } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthService, AuthResult } from '@minikaenguru-ws/common-auth';
import { RegistrationService } from './registration/registration.service';

@Component({
	selector: 'mkv-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {

	envName = environment.envName;
	showEnv = !environment.production;
	api = environment.apiUrl;
	version = environment.version;

	constructor(private authService: AuthService,
		private registrationService: RegistrationService) {

		// TODO: müssen nochmal schauen, ob das ein gutes prefix ist
		const STORAGE_KEY_ID_REFERENCE = 'mkv-app-id-reference';
		sessionStorage.setItem(STORAGE_KEY_ID_REFERENCE, 'anonym');

		const hash = window.location.hash;

		if (hash && hash.indexOf('idToken') > 0) {
			const authResult = this.authService.parseHash(hash) as AuthResult;

			if (authResult.state) {
				if (authResult.state === 'login') {
					this.authService.createSession(authResult);
				}
				if (authResult.state === 'signup') {
					this.registrationService.createUser(authResult);
				}
			} else {
				window.location.hash = '';
			}
		}
	}
}
