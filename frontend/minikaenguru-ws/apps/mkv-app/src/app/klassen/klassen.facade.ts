import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as KlassenActions from './+state/klassen.actions';
import * as KlassenSelectors from './+state/klassen.selectors';
import { Observable } from 'rxjs';
import { Klasse, KlasseEditorModel, KlasseRequestData } from '@minikaenguru-ws/common-components';
import { KlassenService } from './klassen.service';
import { Router } from '@angular/router';
import { KlassenlisteImportReport, KlasseWithID } from './klassen.model';
import { MessageService, ResponsePayload } from '@minikaenguru-ws/common-messages';
import * as KinderActions from '../kinder/+state/kinder.actions';
import * as KinderSelectors from '../kinder/+state/kinder.selectors';
import * as LehrerActions from '../lehrer/+state/lehrer.actions';
import { KinderService } from '../kinder/kinder.service';
import { KinderFacade } from '../kinder/kinder.facade';



@Injectable({
	providedIn: 'root'
})
export class KlassenFacade {

	public klassenGeladen$: Observable<boolean> = this.store.select(KlassenSelectors.klassenGeladen);
	public klassen$: Observable<Klasse[]> = this.store.select(KlassenSelectors.klassen);
	public anzahlKlassen$: Observable<number> = this.store.select(KlassenSelectors.anzahlKlassen);
	public editorModel$: Observable<KlasseEditorModel | undefined> = this.store.select(KlassenSelectors.klasseEditorModel);
	public klassenMap$: Observable<KlasseWithID[]> = this.store.select(KlassenSelectors.klassenMap);
	public selectedKlasse$: Observable<Klasse | undefined> = this.store.select(KlassenSelectors.selectedKlasse);
	public anzahlLoesungszettel$: Observable<number> = this.store.select(KlassenSelectors.anzahlLoesungszettel);
	public klassenimportReport$ : Observable<KlassenlisteImportReport | undefined> = this.store.select(KlassenSelectors.klassenimportReport);

	private loggingOut = false;

	private klassenGeladen = false;

	private kinderGeladen = false;

	constructor(private store: Store<AppState>,
		private kinderFacade: KinderFacade,
		private router: Router,
		private authService: AuthService,
		private kinderService: KinderService,
		private messageService: MessageService,
		private klassenService: KlassenService,
		private errorHandler: GlobalErrorHandlerService
	) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);

		this.klassenGeladen$.subscribe(
			geladen => this.klassenGeladen = geladen
		);

		this.store.select(KinderSelectors.kinderGeladen).subscribe(
			geladen => this.kinderGeladen = geladen
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
		this.store.dispatch(KlassenActions.startEditingKlasse({ klasse: klasse }));
	}

	public insertUpdateKinder(klasse: Klasse): void {

		if (!this.kinderGeladen) {
			this.kinderService.loadKinder(klasse.schulkuerzel).subscribe(
				kinder => this.store.dispatch(KinderActions.allKinderLoaded({ kinder: kinder })),
				(error => {
					this.store.dispatch(KinderActions.finishedWithError());
					this.errorHandler.handleError(error);
				})
			);
		}

		this.store.dispatch(KlassenActions.startAssigningKinder({ klasse: klasse }));
		this.router.navigateByUrl('/kinder/' + klasse.schulkuerzel);
	}

	public cancelEditKlasse(): void {
		this.store.dispatch(KlassenActions.editCancelled());
	}

	public insertKlasse(uuid: string, schulkuerzel: string, editorModel: KlasseEditorModel): void {

		this.store.dispatch(KlassenActions.startLoading());

		const data: KlasseRequestData = {
			uuid: uuid,
			klasse: editorModel,
			schulkuerzel: schulkuerzel
		};

		this.klassenService.insertKlasse(data).subscribe(
			rp => {
				this.store.dispatch(KlassenActions.klasseSaved({ klasse: rp.data }));
				this.messageService.showMessage(rp.message);
			},
			(error => {
				this.store.dispatch(KlassenActions.finishedLoadig());
				this.errorHandler.handleError(error);
			})
		);
	}

	public updateKlasse(uuid: string, schulkuerzel: string, editorModel: KlasseEditorModel): void {

		this.store.dispatch(KlassenActions.startLoading());

		const data: KlasseRequestData = {
			uuid: uuid,
			klasse: editorModel,
			schulkuerzel: schulkuerzel
		};

		this.klassenService.updateKlasse(data).subscribe(
			rp => {
				this.store.dispatch(KlassenActions.klasseSaved({ klasse: rp.data }));
				this.messageService.showMessage(rp.message);
			},
			(error => {
				this.store.dispatch(KlassenActions.finishedLoadig());
				this.errorHandler.handleError(error);
			})
		);
	}

	public deleteKlasse(klasse: Klasse): void {

		this.store.dispatch(KlassenActions.startLoading());

		this.klassenService.deleteKlasse(klasse.uuid).subscribe(
			rp => {
				this.store.dispatch(KinderActions.resetModule());
				this.store.dispatch(KlassenActions.klasseDeleted({ klasse: rp.data }));
				this.messageService.showMessage(rp.message);
			},
			(error => {
				this.store.dispatch(KlassenActions.finishedLoadig());
				this.errorHandler.handleError(error);
			})
		);
	}

	public dateiAusgewaelt(): void {

		this.messageService.clear();

		this.store.dispatch(KlassenActions.dateiAusgewaehlt());

	}

	public klassenlisteImportiert(responsePayload: ResponsePayload): void {

		if (responsePayload.data) {

			const report: KlassenlisteImportReport = responsePayload.data;
			this.store.dispatch(KinderActions.resetModule());
			this.store.dispatch(KlassenActions.klassenlisteImportiert({ report: report }));
		}

		this.messageService.showMessage(responsePayload.message);

	};

	public resetState(): void {
		this.kinderFacade.resetState();
		this.store.dispatch(KlassenActions.resetModule());
		this.store.dispatch(LehrerActions.deselectSchule());
	}


};
