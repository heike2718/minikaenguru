import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as PrivatveranstalterActions from './+state/privatveranstalter.actions';
import { hatZugangZuUnterlagen, aktuelleTeilnahmeGeladen, aktuelleangemeldet, aktuellePrivatteilnahme, loading } from './+state/privatveranstalter.selectors';
import { VeranstalterService } from '../services/veranstalter.service';


@Injectable({ providedIn: 'root' })
export class PrivatveranstalterFacade {

	public hatZugangZuUnterlagen$ = this.appStore.select(hatZugangZuUnterlagen);
	public aktuelleTeilnahmeGeladen$ = this.appStore.select(aktuelleTeilnahmeGeladen);
	public aktuelleangemeldet$ = this.appStore.select(aktuelleangemeldet);
	public aktuellePrivatteilnahme$ = this.appStore.select(aktuellePrivatteilnahme);
	public loading$ = this.appStore.select(loading);

	constructor(private appStore: Store<AppState>,
		private veranstalterService: VeranstalterService,
		private errorHandler: GlobalErrorHandlerService) { }


	public resetState(): void {

		this.appStore.dispatch(PrivatveranstalterActions.resetPrivatveranstalter());

	}

	public loadInitialTeilnahmeinfos(): void {

		this.appStore.dispatch(PrivatveranstalterActions.startLoading());

		this.veranstalterService.loadPrivatveranstalter().subscribe(
			veranstalter => {
				this.appStore.dispatch(PrivatveranstalterActions.privatveranstalterGeladen({ veranstalter: veranstalter }));
			},
			(error => {
				this.appStore.dispatch(PrivatveranstalterActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

}
