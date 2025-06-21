import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AppState } from '../../../reducers';
import { Store, select } from '@ngrx/store';
import { Observable } from 'rxjs';
import { schulenLoaded } from '../../+state/lehrer.selectors';
import { tap, first, finalize, filter } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { LehrerFacade } from '../../lehrer.facade';

@Injectable()
export class SchulenListResolver  {

	private loading = false;

	constructor(private store: Store<AppState>, private lehrerFacade: LehrerFacade) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {


		return this.store.pipe(
			select(schulenLoaded),
			tap(() => {
				this.lehrerFacade.loadSchulen();
			}),
			filter(loaded => loaded),
			first(),
			finalize(() => this.loading = false)
		);
	}
}
