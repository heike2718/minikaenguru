import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { tap, first, finalize } from 'rxjs/operators';
import { TeilnahmenFacade } from '../teilnahmen.facade';
import { AuthService } from '@minikaenguru-ws/common-auth';


@Injectable({ providedIn: 'root' })
export class TeilnahmenListResolver  {

	private loading = false;
	private userSubscription: Subscription = new Subscription();


	constructor(private teilnahmenFacade: TeilnahmenFacade, private authService: AuthService) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		return this.teilnahmenFacade.anonymisierteTeilnahmenGeladen$.pipe(

			tap(
				geladen => {

					if (!geladen) {

						if (!this.loading) {
							this.loading = true;

							this.userSubscription = this.authService.user$.subscribe(

								user => {

									if (user) {

										this.teilnahmenFacade.initTeilnahmen(user.rolle);

									}

								}

							);
						}

					}

				}
			),
			first(),
			finalize(() => {
				this.loading = false;
				if (this.userSubscription) {
					this.userSubscription.unsubscribe();
				}
			})
		);

	}

}
