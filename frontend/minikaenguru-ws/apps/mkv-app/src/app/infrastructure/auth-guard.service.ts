import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Session, isLoggedIn } from '@minikaenguru-ws/common-auth';
import { Injectable } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';


@Injectable({
	providedIn: 'root'
})
export class AuthGuardService implements CanActivate {

	constructor(private router: Router, private store: Store<Session>) { }

	canActivate(
		_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<boolean> {

		return this.store
			.pipe(
				select(isLoggedIn),
				tap(loggedIn => {
					if (!loggedIn) {
						console.log('AuthGuardService - 1');
						this.router.navigateByUrl('');
					} else {
						console.log('AuthGuardService - 2');

					}
				})
			)


	}

}
