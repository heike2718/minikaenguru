import { Injectable, inject } from "@angular/core";
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from "@angular/router";
import { AuthService } from "@minikaenguru-ws/common-auth";
import { Observable } from "rxjs";
import { tap } from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class AdminKatalogeGuardService {

    #authService = inject(AuthService);
    #router = inject(Router);

    canActivate(
		_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<boolean> {

		return this.#authService.isLoggedIn$.pipe(
            tap( loggedIn => {
                if (!loggedIn) {
                    this.#router.navigateByUrl('/forbidden');
                }
            })
        );
	}
}