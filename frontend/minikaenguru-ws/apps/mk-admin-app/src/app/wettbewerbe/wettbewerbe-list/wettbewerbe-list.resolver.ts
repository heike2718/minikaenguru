import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { tap, first, finalize, filter } from 'rxjs/operators';
import { wettbewerbeLoaded } from '../+state/wettbewerbe.selectors';
import { loadWettbewerbe } from '../+state/wettbewerbe.actions';

@Injectable()
export class WettbewerbeListResolver implements Resolve<any> {

	loading = false;

	constructor(private store: Store<AppState>) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


		return this.store.pipe(
			select(wettbewerbeLoaded),
			tap(areLoaded => {
				if (!areLoaded) {
					this.loading = true;
					this.store.dispatch(loadWettbewerbe());
				}
			}),
			filter(loaded => loaded),
			first(),
			finalize(() => this.loading = false)
		);

	}

}
