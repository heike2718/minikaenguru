import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { AppState } from '../reducers';
import { Store, select } from '@ngrx/store';
import { tap } from 'rxjs/operators';
import { AuthService } from './auth.service';
import { isLoggedIn } from './auth.selectors';

@Injectable()
export class AuthGuard implements CanActivate {

	constructor(
		private store: Store<AppState>, private authService: AuthService) {

	}

	canActivate(_route: ActivatedRouteSnapshot, _state: RouterStateSnapshot): Observable<boolean> {

		return this.store
			.pipe(
				select(isLoggedIn),
				tap(loggedIn => {
					if (!loggedIn) {
						console.log('[AuthGuard] going to redirect to login');
						this.authService.logIn();
					} else {
						console.log('[AuthGuard] is logged in');
					}
				})
			);
	}

}
