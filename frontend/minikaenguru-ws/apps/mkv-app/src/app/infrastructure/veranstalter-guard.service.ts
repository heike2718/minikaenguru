import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Session, isLoggedIn, STORAGE_KEY_USER, User, Rolle } from '@minikaenguru-ws/common-auth';
import { Injectable } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';


@Injectable({
	providedIn: 'root'
})
export class VeranstalterGuardService  {

	constructor(private router: Router, private sessionStore: Store<Session>) { }

	canActivate(
		_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<boolean> {

		const obj = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);

		if (obj) {
			const user: User = JSON.parse(obj);

			if (user.rolle === 'PRIVAT' || user.rolle === 'LEHRER') {

				return this.sessionStore
					.pipe(
						select(isLoggedIn),
						tap(loggedIn => {
							if (!loggedIn) {
								this.router.navigateByUrl('');
							}
						})
					);
			} else {
				return of(false);
			}
		} else {
			return of(false);
		}
	}
}
