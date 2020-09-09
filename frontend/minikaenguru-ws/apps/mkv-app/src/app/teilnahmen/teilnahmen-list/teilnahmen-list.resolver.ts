import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, forkJoin, Subscription } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { TeilnahmenFacade } from '../teilnahmen.facade';
import { AuthService } from '@minikaenguru-ws/common-auth';


@Injectable({ providedIn: 'root' })
export class TeilnahmenListResolver implements Resolve<any> {

	private loading = false;
	private userSubscription: Subscription;


	constructor(private teilnahmenFacade: TeilnahmenFacade, private authService: AuthService) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		return this.teilnahmenFacade.anonymisierteTeilnahmenGeladen$.pipe(

			tap(
				geladen => {

					if (!geladen) {

						if (!this.loading) {
							this.loading = true;

							this.userSubscription = this.authService.user$.subscribe(user => {

								if (user) {

									this.teilnahmenFacade.initTeilnahmen(user.rolle);

								}

							}

							)
						}

					}

				}
			),
			first(),
			finalize(() => {
				this.loading = false;
			})
		);

	}

}
