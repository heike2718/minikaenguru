import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { allSchulen, selectedSchule, loading, schuleDashboadModel } from './+state/schulen.selectors';
import { SchulenService } from '../services/schulen.service';
import * as SchulenActions from './+state/schulen.actions';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Schule } from './schulen.model';


@Injectable({ providedIn: 'root' })
export class SchulenFacade {

	public schulen$ = this.appStore.select(allSchulen);
	public selectedSchule$ = this.appStore.select(selectedSchule);
	public schuleDashboadModel$ = this.appStore.select(schuleDashboadModel);

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

	public loadDetails(schule: Schule): void {

		this.appStore.dispatch(SchulenActions.startLoading());

		this.schulenService.loadDetails(schule).subscribe(
			details => {
				this.appStore.dispatch(SchulenActions.schuleDetailsLoaded({ schule: schule, details: details }))
			},
			(error => {
				this.appStore.dispatch(SchulenActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		)
	}

	public resetState(): void {

		this.appStore.dispatch(SchulenActions.resetSchulen());
	}
}
