import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as KlassenActions from './+state/klassen.actions';
import * as KlassenSelectors from './+state/klassen.selectors';
import { Observable } from 'rxjs';
import { Klasse, KlasseEditorModel } from '@minikaenguru-ws/common-components';
import { KlassenService } from './klassen.service';
import { Router } from '@angular/router';
import { KlasseWithID } from './klassen.model';



@Injectable({
	providedIn: 'root'
})
export class KlassenFacade {

	public klassenGeladen$: Observable<boolean> = this.store.select(KlassenSelectors.klassenGeladen);
	public klassen$: Observable<Klasse[]> = this.store.select(KlassenSelectors.klassen);
	public anzahlKlassen$: Observable<number> = this.store.select(KlassenSelectors.anzahlKlassen);
	public editorModel$: Observable<KlasseEditorModel> = this.store.select(KlassenSelectors.klasseEditorModel);
	public klassenMap$: Observable<KlasseWithID[]> = this.store.select(KlassenSelectors.klassenMap);

	private loggingOut = false;

	private klassenGeladen = false;

	constructor(private store: Store<AppState>,
		private router: Router,
		private authService: AuthService,
		private klassenService: KlassenService,
		private errorHandler: GlobalErrorHandlerService
	) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);

		this.klassenGeladen$.subscribe(
			geladen => this.klassenGeladen = geladen
		);
	}

	public loadKlassen(teilnahmenummer: string) {

		if (this.loggingOut) {
			return;
		}

		if (this.klassenGeladen) {
			return;
		}

		this.store.dispatch(KlassenActions.startLoading());

		this.klassenService.loadKlassen(teilnahmenummer).subscribe(
			kl => this.store.dispatch(KlassenActions.allKlassenLoaded({ klassen: kl })),
			(error => {
				this.store.dispatch(KlassenActions.finishedLoadig());
				this.errorHandler.handleError(error);
			})
		);
	}

	public startCreateKlasse(): void {

		this.store.dispatch(KlassenActions.createNewKlasse());
		this.router.navigateByUrl('/klassen/klasse-editor/neu');
	}

	public editKlasse(klasse: Klasse): void {
		this.store.dispatch(KlassenActions.startEditingKlasse({klasse: klasse}));
	}


	public resetState(): void {
		this.store.dispatch(KlassenActions.resetModule());
	}


};
