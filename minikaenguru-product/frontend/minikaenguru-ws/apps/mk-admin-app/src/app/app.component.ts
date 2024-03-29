import { Component } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthService, AuthResult } from '@minikaenguru-ws/common-auth';
import { slideInAnimation } from './animations';
import { Router, RouterOutlet } from '@angular/router';

@Component({
	selector: 'mka-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css'],
	animations: [slideInAnimation]
})
export class AppComponent {

	envName = environment.envName;
	showEnv = this.envName === 'DEV';
	api = environment.apiUrl;
	version = environment.version;

	constructor(public authService: AuthService, private router: Router) {


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

	getAnimationData(outlet: RouterOutlet) {
		return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
	}

	gotoDashboad(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
