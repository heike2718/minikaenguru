import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../environments/environment';
import { AuthService, AuthState, selectIsLoggedIn, selectIsLoggedOut } from '@minikaenguru-ws/common-auth';
import { Store } from '@ngrx/store';

@Component({
	selector: 'mkv-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

	collapsed = true;
	logo: string;

	isLoggedIn$ = this.store.select(selectIsLoggedIn);
	isLoggedOut$ = this.store.select(selectIsLoggedOut);

	@ViewChild(NgbCollapse, { static: true }) navbarToggler: NgbCollapse;

	constructor(private authService: AuthService
		, private store: Store<AuthState>) { }

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
		this.authService.logout();
	}

}
