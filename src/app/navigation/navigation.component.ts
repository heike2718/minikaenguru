import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../auth/auth.service';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AppState } from '../reducers';
import { Router } from '@angular/router';
import { LogService } from 'hewi-ng-lib';
import { HttpErrorHandler } from '../error/http-error-handler.service';
import { isLoggedIn, isLoggedOut } from '../client/client.selectors';
import { logout } from '../client/client.actions';

@Component({
	selector: 'mkadm-navigation',
	templateUrl: './navigation.component.html',
	styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

	activeIndex = 0;
	items: MenuItem[];

	isLoggedIn$: Observable<boolean>;
	isLoggerOut$: Observable<boolean>;


	constructor(private authService: AuthService
		// tslint:disable: align
		, private store: Store<AppState>
		, private router: Router
		, private logger: LogService
		, private errorHandler: HttpErrorHandler) { }

	ngOnInit() {

		this.items = [
			{
				label: 'Home',
				icon: 'pi pi-home',
				routerLink: '/home'
			},
			{
				label: 'Dashboard',
				icon: 'pi pi-th-large',
				routerLink: '/dashboard'
			},
			{
				label: 'About',
				icon: 'pi pi-question',
				routerLink: '/about'
			}
		];

		this.isLoggedIn$ = this.store.pipe(
			// select eliminates any duplicates
			select(isLoggedIn)
		);

		this.isLoggerOut$ = this.store.pipe(
			select(isLoggedOut)
		);
	}

	login() {

		this.logger.debug('login getriggert');
		this.authService.login();
	}

	logout() {
		this.store.dispatch(logout());
	}
}

