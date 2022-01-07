import { Component, OnDestroy, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { Router, RouterOutlet } from '@angular/router';
import { Subscription } from 'rxjs';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';

@Component({
	selector: 'mkod-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

	envName = environment.envName;
	showEnv = this.envName === 'DEV';
	api = environment.apiUrl;
	version = '';

	private versionSubscription: Subscription = new Subscription();

	constructor(private router: Router, private versionService: VersionService) {
		
		const hash = window.location.hash;

		if (hash ) {

			const hashLower = hash.toLowerCase();

			if (hashLower.indexOf('anmeldungen') >= 0) {
				window.location.hash = '';
				this.router.navigateByUrl('/anmeldungen');
			}

			if (hashLower.indexOf('teilnahmen') >= 0) {

				let jahr: number | undefined;

				const keyVal = hashLower.split('=');

				switch (keyVal[0]) {
						case 'jahr': jahr = JSON.parse(keyVal[1]); break;
				}

				if (jahr) {
					window.location.hash = '';
					this.router.navigateByUrl('/teilnahmen/' + jahr);
				}			
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
