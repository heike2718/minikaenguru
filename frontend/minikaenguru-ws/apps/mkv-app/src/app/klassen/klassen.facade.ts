import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as KlassenActions from './+state/klassen.actions';
import * as KlassenSelectors from './+state/klassen.selectors';
import { Observable } from 'rxjs';
import { Klasse } from '@minikaenguru-ws/common-components';



@Injectable({
	providedIn: 'root'
})
export class KlassenFacade {

	public klassenGeladen$: Observable<boolean> = this.store.select(KlassenSelectors.klassenGeladen);
	public klassen$: Observable<Klasse[]> = this.store.select(KlassenSelectors.klassen);
	public anzahlKlassen$: Observable<number> = this.store.select(KlassenSelectors.anzahlKlassen);

	private loggingOut: boolean;

	constructor(private store: Store<AppState>,
		private authService: AuthService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService
	) {

	}

	public loadKlassen(teilnahmenummer: string) {

	}


	public resetState(): void {
		this.store.dispatch(KlassenActions.resetModule());
	}


};
