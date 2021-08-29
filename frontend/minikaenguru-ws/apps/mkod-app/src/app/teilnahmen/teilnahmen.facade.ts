import { TeilnahmenService } from './teilnahmen.service';
import { AppState } from '../reducers';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import * as TeilnahmenActions from './+state/teilnahmen.actions';
import * as TeilnahmenSelectors from './+state/teilnahmen.selectors';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Observable, of, Subscription } from 'rxjs';
import { Anmeldungen, AnmeldungenWithID, AnmeldungenMap } from '../shared/beteiligungen.model';

@Injectable({
	providedIn: 'root'
})
export class TeilnahmenFacade {

	private teilnahmenMapSubscription: Subscription;

	private anmeldungenMap: AnmeldungenWithID[] = [];


    constructor(private store: Store<AppState>,
        private teilnahmenService: TeilnahmenService,
        private errorHandler: GlobalErrorHandlerService) {

			this.teilnahmenMapSubscription = this.store.select(TeilnahmenSelectors.anmeldungenMap).subscribe(
				anmeldungen => this.anmeldungenMap = anmeldungen
			);
		}

    public loadTeilnahmen(jahr: number): Observable<Anmeldungen> {
		
		const map = new AnmeldungenMap(this.anmeldungenMap);

        if (map.has('' + jahr)) {

			return of(map.get('' + jahr));
		}

        this.teilnahmenService.loadTeilnahmen(jahr).subscribe(
			anmeldungen => {
				this.store.dispatch(TeilnahmenActions.teilnahmenLoaded({anmeldungen: anmeldungen}));
				return anmeldungen;
			},
			(error => {
				this.store.dispatch(TeilnahmenActions.finishedWithError());
				this.errorHandler.handleError(error);
				return of(null);
			})
		);

		return of(null);
    }



	public reset(): void {
		this.store.dispatch(TeilnahmenActions.reset());
	}
}
