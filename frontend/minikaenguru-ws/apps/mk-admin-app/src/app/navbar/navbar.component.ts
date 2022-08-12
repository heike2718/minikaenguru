import { Component, OnInit, ViewChild } from '@angular/core';
import { AuthService, AuthState, isLoggedIn, isLoggedOut, Session, user } from '@minikaenguru-ws/common-auth';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { Store } from '@ngrx/store';
import { environment } from '../../environments/environment';
import { LogoutService } from '../services/logout.service';

@Component({
	selector: 'mka-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

	collapsed = true;
	logo: string = '';
	
	isLoggedIn$ = this.authStore.select(isLoggedIn);
	isLoggedOut$ = this.authStore.select(isLoggedOut);
	user$ = this.sessionStore.select(user);
	version = environment.version;

	@ViewChild(NgbCollapse, { static: true }) navbarToggler?: NgbCollapse;

	constructor(private authService: AuthService
		, private authStore: Store<AuthState>
		, private sessionStore: Store<Session>
		, private logoutService: LogoutService) { }

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
		this.logoutService.logout();
	}
}
