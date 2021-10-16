import { TeilnahmenService } from './teilnahmen.service';
import { AppState } from '../reducers';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import * as TeilnahmenActions from './+state/teilnahmen.actions';
import * as TeilnahmenSelectors from './+state/teilnahmen.selectors';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Observable } from 'rxjs';
import { Anmeldungen, AnmeldungenWithID, AnmeldungenMap } from '../shared/beteiligungen.model';

@Injectable({
	providedIn: 'root'
})
export class TeilnahmenFacade {

	private anmeldungenMap: AnmeldungenWithID[] = [];
	
	public selectedAnmeldung$: Observable<Anmeldungen | undefined> = this.store.select(TeilnahmenSelectors.selectedAnmeldung);

    constructor(private store: Store<AppState>,
        private teilnahmenService: TeilnahmenService,
        private errorHandler: GlobalErrorHandlerService) {

			this.store.select(TeilnahmenSelectors.anmeldungenMap).subscribe(
				anmeldungen => this.anmeldungenMap = anmeldungen
			);
		}

    public loadTeilnahmen(jahr: number): void {
		
		const map = new AnmeldungenMap(this.anmeldungenMap);

		const anmeldungen : Anmeldungen | undefined = map.get('' + jahr);

        if (anmeldungen) {

			this.store.dispatch(TeilnahmenActions.teilnahmenSelected({anmeldungen: anmeldungen}));
			return;
		}

		this.store.dispatch(TeilnahmenActions.loadTeilnhahmen({jahr: jahr}))

        this.teilnahmenService.loadTeilnahmen(jahr).subscribe(
			anmeldungen => {
				this.store.dispatch(TeilnahmenActions.teilnahmenLoaded({anmeldungen: anmeldungen}));
			},
			(error => {
				this.store.dispatch(TeilnahmenActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
    }



	public reset(): void {
		this.store.dispatch(TeilnahmenActions.reset());
	}
}
