import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { loading, anonymisierteTeilnahmenGeladen, anonymisierteTeilnahmen } from './+state/privatteilnahmen.selectors';
import * as PrivatteilnahmeActions from './+state/privatteilnahmen.actions';
import { tap } from 'rxjs/operators';


@Injectable({ providedIn: 'root' })
export class PrivatteilnahmenFacade {

	public loading$ = this.appStore.select(loading);
	public anonymisierteTeilnahmenGeladen$ = this.appStore.select(anonymisierteTeilnahmenGeladen);
	public anonymisierteTeilnahmen$ = this.appStore.select(anonymisierteTeilnahmen);

	constructor(private appStore: Store<AppState>,
		private teilnahmenService: TeilnahmenService,
		private errorHandler: GlobalErrorHandlerService) { }

	public ladeAnonymisierteTeilnahmen(teilnahmenummer: string): void {

		this.appStore.dispatch(PrivatteilnahmeActions.startLoading());

		this.anonymisierteTeilnahmenGeladen$.pipe(
			tap(
				geladen => {
					if (!geladen) {
						this.internalLoadTeilnahmen(teilnahmenummer);
					}
				}
			)
		).subscribe();
	}


	private internalLoadTeilnahmen(teilnahmenummer: string) {
		this.teilnahmenService.ladeAnonymisierteTeilnahmen(teilnahmenummer).subscribe(

			teilnahmen => {
				this.appStore.dispatch(PrivatteilnahmeActions.anonymeTeilnahmenGeladen({ anonymeTeilnahmen: teilnahmen }));
			},
			(error => {
				this.appStore.dispatch(PrivatteilnahmeActions.loadFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

}

