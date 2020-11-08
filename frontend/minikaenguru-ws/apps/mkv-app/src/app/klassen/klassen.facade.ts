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

	private loggingOut = false;

	constructor(private store: Store<AppState>,
		private authService: AuthService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService
	) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);
	}

	public loadKlassen(teilnahmenummer: string) {

		if (this.loggingOut) {
			return;
		}

		this.store.dispatch(KlassenActions.startLoading());

		const klassen: Klasse[] = [];
		klassen.push({
			name: '2a',
			schulkuerzel: teilnahmenummer,
			uuid: 'uuid-1',
			anzahlKinder: 0
		});
		klassen.push({
			name: '1b',
			schulkuerzel: teilnahmenummer,
			uuid: 'uuid-2',
			anzahlKinder: 0
		});

		this.store.dispatch(KlassenActions.allKlassenLoaded({ klassen: klassen }));
	}


	public resetState(): void {
		this.store.dispatch(KlassenActions.resetModule());
	}


};
