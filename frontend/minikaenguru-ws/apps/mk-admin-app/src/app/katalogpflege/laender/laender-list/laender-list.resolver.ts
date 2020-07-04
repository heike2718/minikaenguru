import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { laender } from '../../+state/katalogpflege.selectors';

@Injectable()
export class LaenderListResolver implements Resolve<any> {

	private loading = false;

	constructor(private store: Store<AppState>, private katalogFacade: KatalogpflegeFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

	   return this.store.pipe(
		   select(laender),
		   tap(laender => {
			   if (laender.length === 0) {
				   if (!this.loading) {
					   this.loading = true;
					   this.katalogFacade.ladeLaender();
				   }
			   }
		   }),
		   filter(laender => laender.length > 0),
		   first(),
		   finalize(() => this.loading = false)
	   );
	}
}
