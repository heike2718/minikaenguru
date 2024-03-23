import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../reducers';
import { Observable } from 'rxjs';
import { TeilnahmenFacade } from './teilnahmen.facade';
import { selectedWettbewerb } from '../wettbewerbe/+state/wettbewerbe.selectors';
import { filter, first, tap } from 'rxjs/operators';




@Injectable()
export class TeilnahmenJahrResolver  {

    constructor(private store: Store<AppState>, private teilnahmenFacade: TeilnahmenFacade) {}

    resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

            let jahr: number | undefined;

            try {
				jahr = parseInt(route.params.id, 0);
			} catch {
				jahr = undefined;
			}

            return this.store.pipe(
				select(selectedWettbewerb),
				tap(w => {
					if (w && jahr) {
						this.teilnahmenFacade.loadTeilnahmen(jahr);
					}
				}),
				//filter(wettbewerb => wettbewerb && wettbewerb.jahr === jahr),
				first()
			);
    }
}