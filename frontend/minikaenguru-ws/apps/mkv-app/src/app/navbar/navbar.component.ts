import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../environments/environment';
import { AuthService, AuthState, isLoggedIn, isLoggedOut, Session, user } from '@minikaenguru-ws/common-auth';
import { Store } from '@ngrx/store';
import { SchulenFacade } from '../schulen/schulen.facade';

@Component({
	selector: 'mkv-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

	collapsed = true;
	logo: string;

	isLoggedIn$ = this.authStore.select(isLoggedIn);
	isLoggedOut$ = this.authStore.select(isLoggedOut);
	user$ = this.sessionStore.select(user);


	@ViewChild(NgbCollapse, { static: true }) navbarToggler: NgbCollapse;

	constructor(private authService: AuthService
		, private authStore: Store<AuthState>
		, private sessionStore: Store<Session>
		, private schulenFacade: SchulenFacade) { }

	collapseNav() {
		if (this.navbarToggler) {
			this.collapsed = true;
		}
	}

	ngOnInit() {
		this.logo = environment.assetsUrl + '/mja-logo.png';
	}

	login() {
		this.authService.login();
	}

	logout() {
		this.schulenFacade.resetState();
		this.authService.logout();
	}
}
