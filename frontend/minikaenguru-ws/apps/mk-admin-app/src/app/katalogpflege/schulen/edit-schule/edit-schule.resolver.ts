import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Store, select } from '@ngrx/store';
import { AppState } from '../../../reducers';
import { Observable } from 'rxjs';
import { tap, filter, first, finalize } from 'rxjs/operators';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { editSchuleInput } from '../../+state/katalogpflege.selectors';

@Injectable()
export class EditSchuleResolver implements Resolve<any> {

	private loading = false;

	constructor(private store: Store<AppState>, private katalogFacade: KatalogpflegeFacade) { }

	resolve(route: ActivatedRouteSnapshot,
		_state: RouterStateSnapshot): Observable<any> {

		const id = route.paramMap.get('id');

		if (id !== 'neu') {

			return this.store.pipe(
				select(editSchuleInput),
				tap(input => {
					if (!input.loading) {
						if (!input.payload) {
							if (!this.loading) {
								this.loading = true;
								this.katalogFacade.createEditSchulePayload(input.selectedItem);
							}
						}
					}
				}),
				filter(input => input !== undefined),
				first(),
				finalize(() => this.loading = false)
			);
		}

		return this.store.pipe(
			select(editSchuleInput),
			tap(input => {
				if (!input.loading) {
					if (!input.payload) {
						if (!this.loading) {
							this.loading = true;
							this.katalogFacade.createNeueSchulePayload();
						}
					}
				}
			}),
			filter(input => input !== undefined),
			first(),
			finalize(() => this.loading = false)
		);
	}
}
