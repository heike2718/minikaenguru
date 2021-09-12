import { AppState } from '../reducers';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as WettbewerbeActions from './+state/wettbewerbe.actions';
import * as WettbewerbeSelectors from './+state/wettbewerbe.selectors';
import { WettbewerbeService } from './wettbewerbe.service';
import { Wettbewerb } from './wettbewerb.model';
import { Observable } from 'rxjs';
import { Anmeldungen } from '../shared/beteiligungen.model';

@Injectable({
	providedIn: 'root'
})
export class WettbewerbeFacade {

	public wettbewerbe$ = this.store.select(WettbewerbeSelectors.wettbewerbe);
	public selectedWettbewerb$ = this.store.select(WettbewerbeSelectors.selectedWettbewerb);

    constructor(private store: Store<AppState>,
        private wettbewerbeService: WettbewerbeService,
        private errorHandler: GlobalErrorHandlerService) {}

    public loadWettbewerbe(): void {

        this.store.dispatch(WettbewerbeActions.startLoadWettbewerbe());

        this.wettbewerbeService.loadWettbewerbe().subscribe(
			wettbewerbe => {
				this.store.dispatch(WettbewerbeActions.wettbewerbeLoaded({wettbewerbe: wettbewerbe}));
			},
			(error => {
				this.store.dispatch(WettbewerbeActions.loadFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);
    }

	public clearWettbewerbSelection(): void {

		this.store.dispatch(WettbewerbeActions.wettbewerbDeselected());
	}

    public selectWettbewerb(wettbewerb: Wettbewerb): void {

		this.store.dispatch(WettbewerbeActions.wettbewerbSelected({wettbewerb: wettbewerb}));
        
    }

	public reset(): void {
		this.store.dispatch(WettbewerbeActions.reset());
	}
}
