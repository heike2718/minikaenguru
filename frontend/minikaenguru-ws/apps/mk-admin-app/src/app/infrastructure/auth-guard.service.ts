import { Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { Injectable, inject } from '@angular/core';
import { tap } from 'rxjs/operators';
import { Observable } from 'rxjs';


@Injectable({
	providedIn: 'root'
})
export class AuthGuardService {

	#authService = inject(AuthService);
	#router = inject(Router);

	canActivate(
		_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<boolean> {

		return this.#authService.isAuthorized$.pipe(
			tap(authorized => {
				if (!authorized) {
					this.#router.navigateByUrl('/forbidden');
				}
			})
		);
	}
}
