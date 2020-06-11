import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { allSchulen, selectedSchule, loading, schuleDetails } from './+state/schulen.selectors';
import { SchulenService } from '../services/schulen.service';
import * as SchulenActions from './+state/schulen.actions';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Schule } from './schulen.model';


@Injectable({ providedIn: 'root' })
export class SchulenFacade {

	public schulen$ = this.appStore.select(allSchulen);
	public selectedSchule$ = this.appStore.select(selectedSchule);
	public schuleDetails$ = this.appStore.select(schuleDetails);

	public loading$ = this.appStore.select(loading);

	constructor(private appStore: Store<AppState>,
		private schulenService: SchulenService,
		private errorHandler: GlobalErrorHandlerService) {
	}

	public loadSchulen(): void {

		this.appStore.dispatch(SchulenActions.startLoading());

		this.schulenService.findSchulen().subscribe(
			schulen => {
				this.appStore.dispatch(SchulenActions.schulenLoaded({ schulen: schulen }));
			},
			(error => {
				this.appStore.dispatch(SchulenActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public loadDetails(schulkuerzel: string): void {

		this.appStore.dispatch(SchulenActions.startLoading());

		this.schulenService.loadDetails(schulkuerzel).subscribe(
			data => {
				this.appStore.dispatch(SchulenActions.schuleDetailsLoaded({ schule: data }))
			},
			(error => {
				this.appStore.dispatch(SchulenActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		)
	}

	public selectSchule(schule: Schule): void {

		this.appStore.dispatch(SchulenActions.selectSchule({schule: schule}));
	}

	public restoreDetailsFromCache(schulkuerzel: string): void {
		this.appStore.dispatch(SchulenActions.restoreDetailsFromCache({kuerzel: schulkuerzel}));
	}

	public resetSelection(): void {
		this.appStore.dispatch(SchulenActions.deselectSchule());
	}

	public resetState(): void {

		this.appStore.dispatch(SchulenActions.resetSchulen());
	}
}
