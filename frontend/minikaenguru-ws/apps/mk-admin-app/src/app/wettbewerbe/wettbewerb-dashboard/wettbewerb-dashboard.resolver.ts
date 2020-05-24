import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../../reducers';
import { Observable } from 'rxjs';
import { selectedWettbewerb}  from '../+state/wettbewerbe.selectors';
import { tap, filter, first } from 'rxjs/operators';
import { loadWettbewerbDetails } from '../+state/wettbewerbe.actions';

@Injectable()
export class WettbewerbDashboardResolver implements Resolve<any> {

	constructor(private store: Store<AppState>) { }

		resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


			let jahr: number;
			try {
				jahr = parseInt(route.params.id);
			} catch {
				jahr = null;
			}


			return this.store.pipe(
				select(selectedWettbewerb),
				tap(w => {
					if (jahr !== null && ( !w || !w.teilnahmenuebersicht )) {
						this.store.dispatch(loadWettbewerbDetails({jahr: jahr}))
					}
				}),
				filter(wettbewerb => wettbewerb && wettbewerb.jahr === jahr),
				first()
			);

		}
}
