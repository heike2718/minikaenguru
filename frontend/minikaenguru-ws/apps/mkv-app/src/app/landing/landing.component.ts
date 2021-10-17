import { Component, OnInit } from '@angular/core';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';
import { AktuelleMeldungFacade } from '../aktuelle-meldung/aktuelle-meldung.facade';
import { environment } from '../../environments/environment';
import { AuthService, STORAGE_KEY_USER, User } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {

	isLoggedOut$ = this.authService.isLoggedOut$;
	aktuelleMeldungNichtLeer$ = this.aktuelleMeldungFacade.aktuelleMeldungNichtLeer$;
	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	constructor(private wettbewerbFacade: WettbewerbFacade
		, private authService: AuthService
		, private aktuelleMeldungFacade: AktuelleMeldungFacade
		, private router: Router) { }

	ngOnInit(): void {
		this.wettbewerbFacade.ladeAktuellenWettbewerb();
		this.aktuelleMeldungFacade.ladeAktuelleMeldung();

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
