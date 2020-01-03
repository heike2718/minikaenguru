import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { Store, select } from '@ngrx/store';
import { AppState } from '../reducers';
import { isLoggedIn, isLoggedOut } from '../auth/auth.selectors';
import { getApiVersion } from './about.selectors';

@Component({
	selector: 'mkadm-about',
	templateUrl: './about.component.html',
	styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

	version = environment.version;

	pathLogo = environment.assetsUrl + '/mja_logo.png';

	envName = environment.envName;

	apiUrl = environment.apiUrl;

	isLoggedIn$: Observable<boolean>;
	isLoggedOut$: Observable<boolean>;

	apiVersion$: Observable<string>;


	constructor(private store: Store<AppState>) { }

	ngOnInit() {

		this.isLoggedIn$ = this.store.pipe(
			// select eliminates any duplicates
			select(isLoggedIn)
		);

		this.isLoggedOut$ = this.store.pipe(
			select(isLoggedOut)
		);

		this.apiVersion$ = this.store.pipe(
			select(getApiVersion)
		);
	}

}
