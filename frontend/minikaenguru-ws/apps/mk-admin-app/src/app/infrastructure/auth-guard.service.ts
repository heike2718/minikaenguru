import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Session, isAuthorized } from '@minikaenguru-ws/common-auth';
import { Injectable } from '@angular/core';
import { Store, select } from '@ngrx/store';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';


@Injectable({
	providedIn: 'root'
})
export class AuthGuardService  {

	constructor(private router: Router, private store: Store<Session>) { }

	canActivate(
		_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<boolean> {

		return this.store
			.pipe(
				select(isAuthorized),
				tap(authorized => {
					if (!authorized) {
						this.router.navigateByUrl('/forbidden');
					}
				})
			)


	}

}
