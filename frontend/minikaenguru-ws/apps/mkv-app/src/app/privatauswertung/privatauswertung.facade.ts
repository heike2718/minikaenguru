import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as PrivatauswertungSelectors from './+state/privatauswertung.selectors';
import * as PrivatauswertungActions from './+state/privatauswertung.actions';
import { Observable, of, Subscription } from 'rxjs';
import { Kind, KindEditorModel, Duplikatwarnung, PrivatkindRequestData, getKlassenstufeByLabel, getSpracheByLabel } from '@minikaenguru-ws/common-components';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { PrivatauswertungService } from './privatauswertung.service';
import { take, map } from 'rxjs/operators';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { KindWithID } from './privatauswertung.model';
import { ThrowStmt } from '@angular/compiler';
import { Message, MessageService } from '@minikaenguru-ws/common-messages';



@Injectable({
	providedIn: 'root'
})
export class PrivatauswertungFacade {

	public kindEditorModel$: Observable<KindEditorModel> = this.store.select(PrivatauswertungSelectors.kindEditorModel);
	public kinder$: Observable<Kind[]> = this.store.select(PrivatauswertungSelectors.kinder);
	public kinderGeladen$: Observable<boolean> = this.store.select(PrivatauswertungSelectors.kinderGeladen);
	public anzahlKinder$: Observable<number> = this.store.select(PrivatauswertungSelectors.anzahlKinder);
	public duplikatwarnung$: Observable<Duplikatwarnung> = this.store.select(PrivatauswertungSelectors.duplikatwarnung);
	public saveOutcome$: Observable<Message> = this.store.select(PrivatauswertungSelectors.saveOutcome);

	private loggingOut: boolean;

	constructor(private store: Store<AppState>,
		private authService: AuthService,
		private privatauswertungService: PrivatauswertungService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);
	}

	public createNewKind(): void {

		this.store.dispatch(PrivatauswertungActions.createNewKind());
		//TODO navigate to the Editor

	}


	public editKind(kind: Kind): void {

		this.store.dispatch(PrivatauswertungActions.startEditingKind({ kind: kind }));
	}

	public loadKinder(): void {

		if (this.loggingOut) {
			return;
		}

		this.store.dispatch(PrivatauswertungActions.startLoading());

		this.privatauswertungService.loadKinder().subscribe(
			kinder => this.store.dispatch(PrivatauswertungActions.allKinderLoaded({ kinder: kinder })),
			(error => {
				this.store.dispatch(PrivatauswertungActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public pruefeDuplikat(uuid: string, editorModel: KindEditorModel): void {

		this.store.dispatch(PrivatauswertungActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel) as PrivatkindRequestData;

		this.privatauswertungService.checkDuplikat(data).subscribe(
			warnung => this.store.dispatch(PrivatauswertungActions.duplikatGeprueft({ duplikatwarnung: warnung })),
			(error => {
				this.store.dispatch(PrivatauswertungActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public insertKind(uuid: string, editorModel: KindEditorModel): void {

		this.store.dispatch(PrivatauswertungActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel) as PrivatkindRequestData;

		this.privatauswertungService.insertKind(data).subscribe(
			responsePayload => this.store.dispatch(PrivatauswertungActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message })),
			(error) => {
				this.store.dispatch(PrivatauswertungActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);
	}

	public updateKind(uuid: string, editorModel: KindEditorModel): void {

		this.store.dispatch(PrivatauswertungActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel) as PrivatkindRequestData;

		this.privatauswertungService.updateKind(data).subscribe(
			responsePayload => this.store.dispatch(PrivatauswertungActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message })),
			(error) => {
				this.store.dispatch(PrivatauswertungActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);

	}

	public deleteKind(uuid: string): void {

		this.store.dispatch(PrivatauswertungActions.startLoading());

		this.privatauswertungService.deleteKind(uuid).subscribe(
			responsePayload => {

				this.store.dispatch(PrivatauswertungActions.kindDeleted({ kind: responsePayload.data, outcome: responsePayload.message }));
				this.messageService.showMessage(responsePayload.message);
			},
			(error) => {
				this.store.dispatch(PrivatauswertungActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);

	}


	// ////////////////////////////////////// private members //////////////////

	private mapFromEditorModel(uuid: string, editorModel: KindEditorModel): PrivatkindRequestData {

		const data: PrivatkindRequestData = {
			uuid: uuid,
			kind: editorModel
		};

		return data
	}
};



