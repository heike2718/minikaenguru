import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../auth/auth.service';
import { Store, select } from '@ngrx/store';
import { noop, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AppState } from '../reducers';
import { login, logout } from '../auth/auth-actions';
import { Router } from '@angular/router';
import { LogService } from 'hewi-ng-lib';
import { HttpErrorHandler } from '../error/http-error-handler.service';
import { isLoggedIn, isLoggedOut } from '../auth/auth.selectors';

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

		this.authService.getUserProfile()
			.pipe(
				tap(respPayload => {

					this.logger.debug(JSON.stringify(respPayload));
					this.store.dispatch(login({ user: respPayload.data }));
					this.router.navigateByUrl('/about');
				})
			)
			.subscribe(
				noop,
				error => this.errorHandler.handleError(error, 'login')
			);

	}

	logout() {
		this.store.dispatch(logout());
	}
}

