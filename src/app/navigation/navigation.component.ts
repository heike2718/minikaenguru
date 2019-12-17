import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../auth/auth.service';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { AppState } from '../reducers';
import { LogService } from 'hewi-ng-lib';
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
	isLoggedOut$: Observable<boolean>;


	constructor(private authService: AuthService
		// tslint:disable: align
		, private store: Store<AppState>
		, private logger: LogService) { }

	ngOnInit() {

		this.items = [
			{
				label: 'Home',
				icon: 'pi pi-home',
				routerLink: '/home'
			},
			/*
			{
				label: 'Dashboard',
				icon: 'pi pi-th-large',
				routerLink: '/dashboard'
			},
			*/
			{
				label: 'About',
				icon: 'pi pi-question',
				routerLink: '/about'
			},
			{
				label: 'Download',
				icon: 'pi pi-question',
				routerLink: '/files'
			}
		];

		this.isLoggedIn$ = this.store.pipe(
			// select eliminates any duplicates
			select(isLoggedIn)
		);

		this.isLoggedOut$ = this.store.pipe(
			select(isLoggedOut)
		);
	}

	login() {

		this.logger.debug('[Navigation Component] logIn getriggert');
		this.authService.logIn();
	}

	logout() {
		this.logger.debug('[Navigation Component] logOut getriggert');
		this.authService.logOut();
	}
}

