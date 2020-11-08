import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable, forkJoin } from 'rxjs';
import { schulenLoaded } from '../../+state/lehrer.selectors';
import { tap, first, finalize, filter } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { LehrerFacade } from '../../lehrer.facade';

@Injectable()
export class SchulenListResolver implements Resolve<any> {

	private loading = false;

	constructor(private store: Store<AppState>, private schulenFacade: LehrerFacade) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


		return this.store.pipe(
			select(schulenLoaded),
			tap(areLoaded => {
				if (!areLoaded) {
					if (!this.loading) {
						this.loading = true;
						this.schulenFacade.loadSchulen();
					}
				}
			}),
			filter(loaded => loaded),
			first(),
			finalize(() => this.loading = false)
		);
	}
}
