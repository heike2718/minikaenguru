import { Component, OnDestroy, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { environment } from '../environments/environment';
import { AuthService, AuthResult } from '@minikaenguru-ws/common-auth';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { RouterOutlet } from '@angular/router';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkv-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

	envName = environment.envName;
	showEnv = environment.envName === 'DEV';
	api = environment.apiUrl;
	version = '';

	private versionSubscription: Subscription = new Subscription();

	constructor(private authService: AuthService
		, private versionService: VersionService
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
					window.location.hash = '';
					this.messageService.info('Ihr Benutzerkonto wurde angelegt und muss noch aktiviert werden. Bitte schauen Sie in Ihrem Postfach nach.')					
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
