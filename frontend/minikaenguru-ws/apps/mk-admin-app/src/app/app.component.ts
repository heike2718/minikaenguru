import { Component } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthService, AuthResult } from '@minikaenguru-ws/common-auth';

@Component({
	selector: 'mka-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {

	envName = environment.envName;
	showEnv = !environment.production;
	api = environment.apiUrl;
	version = environment.version;

	constructor(private authService: AuthService) {


		this.authService.clearOrRestoreSession();

		const hash = window.location.hash;

		if (hash && hash.indexOf('idToken') > 0) {
			const authResult = this.authService.parseHash(hash) as AuthResult;

			if (authResult.state) {
				if (authResult.state === 'login') {
					this.authService.createSession(authResult);

				}
			} else {
				window.location.hash = '';
			}
		}
	}

}
