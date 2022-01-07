import { Component, OnDestroy, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthService, AuthResult } from '@minikaenguru-ws/common-auth';
import { slideInAnimation } from './animations';
import { RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';

@Component({
	selector: 'mka-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css'],
	animations: [slideInAnimation]
})
export class AppComponent implements OnInit, OnDestroy{

	envName = environment.envName;
	showEnv = this.envName === 'DEV';
	api = environment.apiUrl;
	version = '';

	private versionSubscription: Subscription = new Subscription();

	constructor(private authService: AuthService, private versionService: VersionService) {


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

	ngOnInit(): void {
		this.versionSubscription = this.versionService.ladeExpectedGuiVersion().subscribe(
			v => this.version = v
		);		
	}

	ngOnDestroy(): void {
		
		this.versionSubscription.unsubscribe();
	}

	getAnimationData(outlet: RouterOutlet) {
		return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
	}

}
