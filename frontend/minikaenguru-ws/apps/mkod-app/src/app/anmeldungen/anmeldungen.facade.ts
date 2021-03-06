import { Injectable } from '@angular/core';
import { AnmeldungenService } from './anmeldungen.service';
import { AppState } from '../reducers';
import { Store } from '@ngrx/store';
import { anmeldungenState, anmeldungenLoading } from './+state/anmeldungen.selectors';
import * as AnmeldungenActions from './+state/anmeldungen.actions';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';

@Injectable({
	providedIn: 'root'
})
export class AnmeldungenFacade {

	public anmeldungenState$ = this.store.select(anmeldungenState);

	public loading$ = this.store.select(anmeldungenLoading);

	private alreadyLoaded = false;

	constructor(private store: Store<AppState>,
		private anmeldungenService: AnmeldungenService,
		private errorHandler: GlobalErrorHandlerService) { }


	public loadAll(): void {

		if (this.alreadyLoaded) {
			return;
		}

		this.store.dispatch(AnmeldungenActions.loadAnmeldungen());

		this.anmeldungenService.loadAnmeldungen().subscribe(
			anmeldungen => {
				this.alreadyLoaded = true;
				this.store.dispatch(AnmeldungenActions.anmeldungenLoaded({anmeldungen: anmeldungen}));
			},
			(error => {
				this.store.dispatch(AnmeldungenActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public reset(): void {
		this.store.dispatch(AnmeldungenActions.reset());
	}
}
