import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, } from 'rxjs';
import { tap, first, finalize } from 'rxjs/operators';
import { KlassenFacade } from '../klassen.facade';

@Injectable()
export class KlassenListResover implements Resolve<any> {


	private loading = false;

	constructor(private klassenFacade: KlassenFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


		const teilnahmenummer = route.paramMap.get('schulkuerzel');

		return this.klassenFacade.klassenGeladen$.pipe(

			tap(

				geladen => {

					if (!geladen) {
						if (!this.loading) {
							this.loading = true;
							this.klassenFacade.loadKlassen(teilnahmenummer);
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
