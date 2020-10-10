import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { Router } from '@angular/router';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Observable } from 'rxjs';

import { SchulteilnahmenService } from './schulteilnahmen.service';
import * as SchulteilnamenActions from './+state/schulteilnahmen.actions';
import * as SchulteilnahmenSelectors from './+state/schulteilnahmen.selectors';
import { SchuleAdminOverview, SchuleAdminOverviewWithID, SchulenOverviewMap } from './schulteilnahmen.model';
import { take } from 'rxjs/operators';

@Injectable({
	providedIn: 'root'
})
export class SchulteilnahmenFacade {

	public schuleOverview$: Observable<SchuleAdminOverview> = this.store.select(SchulteilnahmenSelectors.selectedSchule);

	private schulenMap: SchuleAdminOverviewWithID[];

	constructor(private schulteilnahmenService: SchulteilnahmenService,
		private store: Store<AppState>,
		private router: Router,
		private errorService: GlobalErrorHandlerService) {


		this.store.select(SchulteilnahmenSelectors.schulenMap).subscribe(
			map => this.schulenMap = map
		);

	}

	public clearSchuleSelection(): void {

		this.store.dispatch(SchulteilnamenActions.resetSchulteilnahmen());

	}

	public findOrLoadSchuleAdminOverview(schulkuerzel: string) {

		const map = new SchulenOverviewMap(this.schulenMap);

		if (map.has(schulkuerzel)) {

			this.store.dispatch(SchulteilnamenActions.schuleOverviewLoaded({ schuleAdminOverview: map.get(schulkuerzel) }));
			this.router.navigateByUrl('/schulteilnahmen');
		} else {

			this.store.dispatch(SchulteilnamenActions.startLoadSchule());

			this.schulteilnahmenService.loadSchuleAdminOverview(schulkuerzel).pipe(
				take(1)
			).subscribe(
				(overview: SchuleAdminOverview) => {
					this.store.dispatch(SchulteilnamenActions.schuleOverviewLoaded({ schuleAdminOverview: overview }));
					this.router.navigateByUrl('/schulteilnahme');
				},
				(error => {
					this.store.dispatch(SchulteilnamenActions.loadSchuleFinishedWithError());
					this.errorService.handleError(error);
				})
			)
		}
	}



}
