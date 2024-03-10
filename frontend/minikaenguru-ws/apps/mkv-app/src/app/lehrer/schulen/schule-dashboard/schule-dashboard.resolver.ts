import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { LehrerFacade } from '../../lehrer.facade';
import { schuleDetails } from '../../+state/lehrer.selectors';


@Injectable()
export class SchuleDashboardResolver  {

	private loading = false;

	constructor(private store: Store<AppState>, private lehrerFacade: LehrerFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		const kuerzel = route.params.kuerzel;

		return this.store.pipe(

			select(schuleDetails),
			tap(details => {

				if (!details) {
					if (!this.loading) {
						this.loading = true;
						this.lehrerFacade.loadDetails(kuerzel);
					}
				} else {

					this.lehrerFacade.restoreDetailsFromCache(kuerzel);

					// if (details.kuerzel !== kuerzel) {
					// 	if (!this.loading) {
					// 		this.loading = true;
					// 		this.lehrerFacade.loadDetails(kuerzel);
					// 	}
					// } else {

					// 	this.lehrerFacade.restoreDetailsFromCache(kuerzel);
					// }
				}
			}),
			filter(d => d !== undefined),
			first(),
			finalize(() => this.loading = false)
		);

	}
}
