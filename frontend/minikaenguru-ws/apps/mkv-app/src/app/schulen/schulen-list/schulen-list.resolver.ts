import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { schulenLoaded } from '../+state/schulen.selectors';
import { tap, first, finalize, filter } from 'rxjs/operators';
import { loadSchulen } from '../+state/schulen.actions';
import { Injectable } from '@angular/core';

@Injectable()
export class SchulenListResolver implements Resolve<any> {

	loading = false;

	constructor(private store: Store<AppState>) {

	}
	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {
		return this.store.pipe(
			select(schulenLoaded),
			tap(areLoaded => {
				if (!areLoaded) {
					// if (!this.loading) {
						this.loading = true;
						this.store.dispatch(loadSchulen())
					// }
				}
			}),
			filter(loaded => loaded),
			first(),
			finalize(() => this.loading = false)
		);
	}
}