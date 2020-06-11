import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { SchulenFacade } from '../schulen.facade';
import { schuleDetails } from '../+state/schulen.selectors';
import { tap, filter, first, finalize } from 'rxjs/operators';


@Injectable()
export class SchuleDashboardResolver implements Resolve<any> {

	private loading = false;

	constructor(private store: Store<AppState>, private schulenFacade: SchulenFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		const kuerzel = route.params.kuerzel;

		return this.store.pipe(

			select(schuleDetails),
			tap(details => {

				if (!details) {
					if (!this.loading) {
						this.loading = true;
						this.schulenFacade.loadDetails(kuerzel);
					}
				} else {

					if (details.kuerzel !== kuerzel) {
						if (!this.loading) {
							this.loading = true;
							this.schulenFacade.loadDetails(kuerzel);
						}
					} else {

						this.schulenFacade.restoreDetailsFromCache(kuerzel);
					}
				}
			}),
			filter(d => d !== undefined),
			first(),
			finalize(() => this.loading = false)
		);

	}
}
