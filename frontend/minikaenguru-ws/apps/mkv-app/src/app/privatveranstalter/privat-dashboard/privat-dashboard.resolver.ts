import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { PrivatveranstalterFacade } from '../privatveranstalter.facade';
import { aktuelleTeilnahmeGeladen } from '../+state/privatveranstalter.selectors';


@Injectable()
export class PrivatDashboardResolver implements Resolve<any> {

private loading = false;

	constructor(private store: Store<AppState>, private veranstalterFacade: PrivatveranstalterFacade) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		return this.store.pipe(

			select(aktuelleTeilnahmeGeladen),
			tap(
				value => {

					if (!value) {
						if (!this.loading) {
							this.loading = true;
							this.veranstalterFacade.loadInitialTeilnahmeinfos()
						}
					}
				}
			),
			filter(v => v !== undefined),
			first(),
			finalize(() => this.loading = false)
		);

	}


}
