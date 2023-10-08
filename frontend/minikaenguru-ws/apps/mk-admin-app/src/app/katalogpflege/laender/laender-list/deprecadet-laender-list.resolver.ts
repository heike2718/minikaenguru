import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { laender } from '../../+state/katalogpflege.selectors';

@Injectable()
export class DeprecatedLaenderListResolver  {

	private loading = false;

	constructor(private store: Store<AppState>, private katalogFacade: KatalogpflegeFacade) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

	   return this.store.pipe(
		   select(laender),
		   tap(theLaender => {
			   if (theLaender.length === 0) {
				   if (!this.loading) {
					   this.loading = true;
					   this.katalogFacade.ladeLaender();
				   }
			   }
		   }),
		   filter(theLaender => theLaender.length > 0),
		   first(),
		   finalize(() => this.loading = false)
	   );
	}
}
