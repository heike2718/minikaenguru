import { Component, OnDestroy, OnInit } from '@angular/core';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';
import { AktuelleMeldungFacade } from '../aktuelle-meldung/aktuelle-meldung.facade';
import { environment } from '../../environments/environment';
import { AuthService, STORAGE_KEY_GUI_VERSION, STORAGE_KEY_USER, User } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';
import { Subscription } from 'rxjs';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {

	isLoggedOut$ = this.authService.isLoggedOut$;
	aktuelleMeldungNichtLeer$ = this.aktuelleMeldungFacade.aktuelleMeldungNichtLeer$;
	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;
	version = '';

	private versionSubscription: Subscription = new Subscription();

	constructor(private wettbewerbFacade: WettbewerbFacade
		, private authService: AuthService
		, private aktuelleMeldungFacade: AktuelleMeldungFacade
		, private versionService: VersionService
		, private logger: LogService
		, private router: Router) { }

	ngOnInit(): void {
		this.aktuelleMeldungFacade.ladeAktuelleMeldung();
		this.wettbewerbFacade.ladeAktuellenWettbewerb();
		
		const obj = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);

		if (obj) {
			const user: User = JSON.parse(obj);

			if (user.rolle === 'LEHRER') {
				this.router.navigateByUrl('/lehrer/dashboard');
			}

			if (user.rolle === 'PRIVAT') {
				this.router.navigateByUrl('/privat/dashboard');
			}
		}

		this.versionSubscription = this.versionService.ladeExpectedGuiVersion().subscribe(
			v => {

				const storedGuiVersion = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_GUI_VERSION);
				this.version = v;

				if (this.version !== storedGuiVersion) {
					this.versionService.storeGuiVersionAndReloadApp(environment.storageKeyPrefix + STORAGE_KEY_GUI_VERSION, this.version);
				} else {
					this.logger.info('GUI-Version ist aktuell');
				}
			}
		);
	}

	ngOnDestroy(): void {
		this.versionSubscription.unsubscribe();
	}

	login() {
		this.authService.login();
	}

	gotoRegistrierung(): void {
		this.router.navigateByUrl('/registrierung');
	}

	gotoInfo(): void {
		this.router.navigateByUrl('/info');
	}
}
