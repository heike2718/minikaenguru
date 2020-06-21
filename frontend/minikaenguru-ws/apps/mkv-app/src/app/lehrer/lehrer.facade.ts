import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { alleSchulen, selectedSchule, loading, schuleDetails, hatZugangZuUnterlagen } from './+state/lehrer.selectors';
import { SchulenService } from '../services/schulen.service';
import * as LehrerActions from './+state/lehrer.actions';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Schule } from './schulen/schulen.model';
import { VeranstalterService } from '../services/veranstalter.service';


@Injectable({ providedIn: 'root' })
export class LehrerFacade {


	public hatZugangZuUnterlagen$ = this.appStore.select(hatZugangZuUnterlagen);

	public schulen$ = this.appStore.select(alleSchulen);
	public selectedSchule$ = this.appStore.select(selectedSchule);
	public schuleDetails$ = this.appStore.select(schuleDetails);

	public loading$ = this.appStore.select(loading);

	constructor(private appStore: Store<AppState>,
		private veranstalterService: VeranstalterService,
		private schulenService: SchulenService,
		private errorHandler: GlobalErrorHandlerService) {
	}

	public ladeZugangUnterlagen(): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.veranstalterService.getZugangsstatusUnterlagen().subscribe(
			zugang => {
				this.appStore.dispatch(LehrerActions.zugangsstatusUnterlagenGeladen({ hatZugang: zugang }))
			},
			(error => {
				this.errorHandler.handleError(error);
			})
		);
	}

	public loadSchulen(): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.schulenService.findSchulen().subscribe(
			schulen => {
				this.appStore.dispatch(LehrerActions.schulenLoaded({ schulen: schulen }));
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}


	public loadDetails(schulkuerzel: string): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.schulenService.loadDetails(schulkuerzel).subscribe(
			data => {
				this.appStore.dispatch(LehrerActions.schuleDetailsLoaded({ schule: data }))
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		)
	}

	public selectSchule(schule: Schule): void {

		this.appStore.dispatch(LehrerActions.selectSchule({schule: schule}));
	}

	public restoreDetailsFromCache(schulkuerzel: string): void {
		this.appStore.dispatch(LehrerActions.restoreDetailsFromCache({kuerzel: schulkuerzel}));
	}

	public resetSelection(): void {
		this.appStore.dispatch(LehrerActions.deselectSchule());
	}

	public resetState(): void {

		this.appStore.dispatch(LehrerActions.resetLehrer());
	}

}
