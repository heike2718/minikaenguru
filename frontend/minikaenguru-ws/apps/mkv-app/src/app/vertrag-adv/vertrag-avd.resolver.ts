import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { VertragAdvFacade } from './vertrag-adv.facade';
import { first, tap, finalize } from 'rxjs/operators';
import { Injectable } from '@angular/core';

@Injectable({
	providedIn: 'root'
})
export class VertragAdvResolver  {

	private loading = false;

	constructor(private vertragAdvFacade: VertragAdvFacade) { }

	resolve(_route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		return this.vertragAdvFacade.selectedSchule$.pipe(

			tap(
				schule => {

					if (schule) {

						if (!this.loading) {
							this.loading = true;
							this.vertragAdvFacade.initEditorModel(schule);
						}
					}
				}
			),
			first(),
			finalize(() => {
				this.loading = false;
			})
		);
	}
}
