import { Component, OnInit } from '@angular/core';
import { JWTService } from 'hewi-ng-lib';
import { AuthService } from './auth/auth.service';
import { STORAGE_KEY_USER } from './auth/model/user.model';
import { Store } from '@ngrx/store';
import { AppState } from './reducers';
import { loadUser } from './auth/auth.actions';

@Component({
	selector: 'mkadm-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

	title = 'mkadmin-app';

	constructor(private authService: AuthService
		, private store: Store<AppState>
		, private jwtService: JWTService) { }

	ngOnInit(): void {

		const user = localStorage.getItem(STORAGE_KEY_USER);

		if (user) {
			this.store.dispatch(loadUser({ user: JSON.parse(user) }));
		}

		// nach dem redirect vom AuthProvider ist das die Stelle, an der die Anwendung wieder ankommt.
		// Daher hier redirect-URL parsen
		const hash = window.location.hash;

		if (hash && hash.indexOf('idToken') > 0) {
			const authResult = this.jwtService.parseHash(hash);
			this.authService.createSession(authResult);
		}
	}
}
