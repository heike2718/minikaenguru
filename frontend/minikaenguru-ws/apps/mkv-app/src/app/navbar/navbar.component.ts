import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../environments/environment';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { LehrerFacade } from '../lehrer/lehrer.facade';
import { PrivatveranstalterFacade } from '../privatveranstalter/privatveranstalter.facade';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';
import { LogoutService } from '../services/logout.service';
import { MessageService } from '@minikaenguru-ws/common-messages';

@Component({
	selector: 'mkv-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

	collapsed = true;
	logo: string;

	activateRegistration = true;
	// activateRegistration = false;

	isLoggedIn$ = this.authService.isLoggedIn$;
	isLoggedOut$ = this.authService.isLoggedOut$
	user$ = this.authService.user$;
	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;


	@ViewChild(NgbCollapse, { static: true }) navbarToggler: NgbCollapse;

	constructor(private authService: AuthService
		, private logoutService: LogoutService
		, private wettbewerbFacade: WettbewerbFacade) { }

	collapseNav() {
		if (this.navbarToggler) {
			this.collapsed = true;
		}
	}

	ngOnInit() {
		this.logo = environment.assetsUrl + '/mja-logo.png';
		this.wettbewerbFacade.ladeAktuellenWettbewerb();
	}

	login() {
		this.authService.login();
	}

	logout() {
		this.logoutService.logout();
	}

	gotProfilApp(): void {
		this.logout();
		window.location.href = environment.profileUrl;
	}
}
