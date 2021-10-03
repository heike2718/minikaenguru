import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../reducers';
import { Observable } from 'rxjs';
import { TeilnahmenFacade } from './teilnahmen.facade';
import { selectedWettbewerb } from '../wettbewerbe/+state/wettbewerbe.selectors';
import { filter, first, tap } from 'rxjs/operators';
import { loadTeilnhahmen } from './+state/teilnahmen.actions';




@Injectable()
export class TeilnahmenJahrResolver implements Resolve<any> {

    constructor(private store: Store<AppState>, private teilnahmenFacade: TeilnahmenFacade) {}

    resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

            let jahr: number;

            try {
				jahr = parseInt(route.params.id, 0);
			} catch {
				jahr = undefined;
			}

            return this.store.pipe(
				select(selectedWettbewerb),
				tap(_w => {
					if (jahr !== null) {
						this.teilnahmenFacade.loadTeilnahmen(jahr);
					}
				}),
				filter(wettbewerb => wettbewerb && wettbewerb.jahr === jahr),
				first()
			);
    }
}