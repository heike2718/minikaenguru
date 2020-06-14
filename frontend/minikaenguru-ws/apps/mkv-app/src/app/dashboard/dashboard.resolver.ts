import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { TeilnahmenFacade } from '../teilnahmen/teilnahmen.facade';
import { hatZugangZuUnterlagen } from '../teilnahmen/+state/teilnahmen.selectors';


@Injectable()
export class DashboardResolver implements Resolve<any> {

	private loading = false;

	constructor(private store: Store<AppState>, private teilnahmenFacade: TeilnahmenFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		return this.store.pipe(

			select(hatZugangZuUnterlagen),
			tap(
				value => {

					if (value === undefined) {
						if (!this.loading) {
							this.loading = true;
							this.teilnahmenFacade.ladeStatusZugangUnterlagen();
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
