import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../environments/environment';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { SchulenFacade } from '../schulen/schulen.facade';
import { TeilnahmenFacade } from '../teilnahmen/teilnahmen.facade';

@Component({
	selector: 'mkv-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

	collapsed = true;
	logo: string;

	isLoggedIn$ = this.authService.isLoggedIn$;
	isLoggedOut$ = this.authService.isLoggedOut$
	user$ = this.authService.user$;
	aktuellerWettbewerb$ = this.teilnahmenFacade.aktuellerWettbewerb$;


	@ViewChild(NgbCollapse, { static: true }) navbarToggler: NgbCollapse;

	constructor(private authService: AuthService
		, private schulenFacade: SchulenFacade
		, private teilnahmenFacade: TeilnahmenFacade) { }

	collapseNav() {
		if (this.navbarToggler) {
			this.collapsed = true;
		}
	}

	ngOnInit() {
		this.logo = environment.assetsUrl + '/mja-logo.png';
		this.teilnahmenFacade.ladeAktuellenWettbewerb();
	}

	login() {
		this.authService.login();
	}

	logout() {
		this.schulenFacade.resetState();
		this.teilnahmenFacade.resetState();
		this.authService.logout();
	}
}
