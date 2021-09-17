import { Component } from '@angular/core';
import { environment } from '../environments/environment';
import { Router, RouterOutlet } from '@angular/router';

@Component({
	selector: 'mkod-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {

	envName = environment.envName;
	showEnv = this.envName === 'DEV';
	api = environment.apiUrl;
	version = environment.version;

	constructor(private router: Router) {
		
		const hash = window.location.hash;

		if (hash ) {

			const hashLower = hash.toLowerCase();

			if (hashLower.indexOf('anmeldungen') >= 0) {
				window.location.hash = '';
				router.navigateByUrl('/anmeldungen');
			}

			if (hashLower.indexOf('teilnahmen') >= 0) {

				let jahr: number;

				const keyVal = hashLower.split('=');

				switch (keyVal[0]) {
						case 'jahr': jahr = JSON.parse(keyVal[1]); break;
				}

				if (jahr) {
					window.location.hash = '';
					router.navigateByUrl('/teilnahmen/' + jahr);
				}			
			}
		}
	}

	getAnimationData(outlet: RouterOutlet) {
		return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
	}

}
