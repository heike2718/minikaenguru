import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { filter, finalize, first, tap } from 'rxjs/operators';
import { teilnahmenummernLoaded } from './+state/veranstalter.selectors';
import * as veranstalterActions from './+state/veranstalter.actions';
import { AppState } from '../reducers';


@Injectable({
	providedIn: 'root'
})
export class DashboardResolver implements Resolve<any> {

	loading = false;

	constructor(private store: Store<AppState>) {

	}

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		console.log('[DashboardResolver] ' + _route.pathFromRoot);

		return this.store
			.pipe(
				select(teilnahmenummernLoaded),
				tap(loaded => {
					if (!this.loading && !loaded) {
						this.loading = true;
						this.store.dispatch(veranstalterActions.loadTeilnahmenummern());
					}
				}),
				filter(loaded => loaded),
				first(),
				finalize(() => this.loading = false)
			);
	}
}
