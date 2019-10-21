import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from './reducers';
import { login } from './auth/auth-actions';

@Component({
	selector: 'mkadm-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

	title = 'mkadmin-app';

	constructor(private store: Store<AppState>) { }

	ngOnInit(): void {

		const userProfile = localStorage.getItem('user');

		if (userProfile) {
			this.store.dispatch(login({
				user: JSON.parse(userProfile)
			}));
		}
	}

}
