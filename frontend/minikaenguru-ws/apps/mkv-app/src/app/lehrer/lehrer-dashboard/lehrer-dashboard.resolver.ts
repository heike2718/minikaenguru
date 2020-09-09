import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { LehrerFacade } from '../lehrer.facade';
import { lehrer } from '../+state/lehrer.selectors';

@Injectable()
export class LehrerDashboardResolver implements Resolve<any> {

	private loading = false;

	constructor(private store: Store<AppState>, private lehrerFacade: LehrerFacade) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		return this.store.pipe(

			select(lehrer),
			tap(
				value => {

					if (!value) {
						if (!this.loading) {
							this.loading = true;
							this.lehrerFacade.ladeLehrer();
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
