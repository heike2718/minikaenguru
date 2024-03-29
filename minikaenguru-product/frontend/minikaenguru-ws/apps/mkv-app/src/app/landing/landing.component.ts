import { Component, OnDestroy, OnInit } from '@angular/core';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';
import { AktuelleMeldungFacade } from '../aktuelle-meldung/aktuelle-meldung.facade';
import { environment } from '../../environments/environment';
import { AuthService, STORAGE_KEY_USER, User } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';

@Component({
	selector: 'mkv-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {

	isLoggedOut$ = this.authService.isLoggedOut$;
	aktuelleMeldungNichtLeer$ = this.aktuelleMeldungFacade.aktuelleMeldungNichtLeer$;
	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	constructor(private wettbewerbFacade: WettbewerbFacade
		, public authService: AuthService
		, private aktuelleMeldungFacade: AktuelleMeldungFacade
		, public versionService: VersionService
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

		this.versionService.ladeExpectedGuiVersion();
	}

	ngOnDestroy(): void {
		
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
