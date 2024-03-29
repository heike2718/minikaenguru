import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { tap, first, finalize } from 'rxjs/operators';
import { KinderFacade } from '../kinder.facade';

@Injectable()
export class KinderListResolver  {


	private loading = false;

	constructor(private kinderFacade: KinderFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		const teilnahmenummer: string | null = route.paramMap.get('teilnahmenummer');

		return this.kinderFacade.kinderGeladen$.pipe(

			tap(

				geladen => {

					if (!geladen) {
						if (teilnahmenummer && !this.loading) {
							this.loading = true;

							this.kinderFacade.loadKinder(teilnahmenummer);
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

