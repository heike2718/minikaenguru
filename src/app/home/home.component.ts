import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Store, select } from '@ngrx/store';
import { AppState } from '../reducers';
import { isLoggedIn, isLoggedOut } from '../auth/auth.selectors';

@Component({
	selector: 'mkadm-home',
	templateUrl: './home.component.html',
	styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {

	isLoggedIn$: Observable<boolean>;
	isLoggedOut$: Observable<boolean>;

	constructor(private store: Store<AppState>) { }

	ngOnInit() {

		this.isLoggedIn$ = this.store.pipe(
			// select eliminates any duplicates
			select(isLoggedIn)
		);

		this.isLoggedOut$ = this.store.pipe(
			select(isLoggedOut)
		);
	}

}
