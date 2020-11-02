import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { tap, first, finalize } from 'rxjs/operators';
import { PrivatauswertungFacade } from '../privatauswertung.facade';

@Injectable()
export class KinderListResolver implements Resolve<any> {


	private loading = false;

	constructor(private privatauswertungFacade: PrivatauswertungFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		const teilnahmenummer = route.paramMap.get('teilnahmenummer');

		return this.privatauswertungFacade.kinderGeladen$.pipe(

			tap(

				geladen => {

					if (!geladen) {
						if (!this.loading) {
							this.loading = true;

							this.privatauswertungFacade.loadKinder(teilnahmenummer);
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
};

