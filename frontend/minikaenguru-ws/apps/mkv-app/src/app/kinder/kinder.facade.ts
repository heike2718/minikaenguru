import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as KinderSelectors from './+state/kinder.selectors';
import * as KinderActions from './+state/kinder.actions';
import { Observable, of, Subscription, from } from 'rxjs';
import { Kind, KindEditorModel, Duplikatwarnung, PrivatkindRequestData, getKlassenstufeByLabel, getSpracheByLabel, TeilnahmeIdentifier } from '@minikaenguru-ws/common-components';
import { AuthService, User, STORAGE_KEY_USER, Rolle } from '@minikaenguru-ws/common-auth';
import { KinderService } from './kinder.service';
import { take, map } from 'rxjs/operators';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { KindWithID } from './kinder.model';
import { ThrowStmt } from '@angular/compiler';
import { Message, MessageService } from '@minikaenguru-ws/common-messages';
import { environment } from '../../environments/environment';
import { Wettbewerb } from '../wettbewerb/wettbewerb.model';
import { Teilnahmeart } from 'libs/common-components/src/lib/common-components.model';



@Injectable({
	providedIn: 'root'
})
export class KinderFacade {

	public teilnahmeIdentifier$: Observable<TeilnahmeIdentifier> = this.store.select(KinderSelectors.teilnahmeIdentifier);
	public kindEditorModel$: Observable<KindEditorModel> = this.store.select(KinderSelectors.kindEditorModel);
	public kinder$: Observable<Kind[]> = this.store.select(KinderSelectors.kinder);
	public kinderGeladen$: Observable<boolean> = this.store.select(KinderSelectors.kinderGeladen);
	public anzahlKinder$: Observable<number> = this.store.select(KinderSelectors.anzahlKinder);
	public duplikatwarnung$: Observable<Duplikatwarnung> = this.store.select(KinderSelectors.duplikatwarnung);
	public saveOutcome$: Observable<Message> = this.store.select(KinderSelectors.saveOutcome);

	private loggingOut: boolean;

	private wettbewerb: Wettbewerb;

	constructor(private store: Store<AppState>,
		private authService: AuthService,
		private kinderService: KinderService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);

		this.wettbewerb = JSON.parse(localStorage.getItem(environment.storageKeyPrefix + 'wettbewerb'));
	}

	public createNewKind(): void {

		this.store.dispatch(KinderActions.createNewKind());
		//TODO navigate to the Editor

	}


	public editKind(kind: Kind): void {

		this.store.dispatch(KinderActions.startEditingKind({ kind: kind }));
	}

	public cancelEditKind(): void {

		this.store.dispatch(KinderActions.editCancelled());
	}

	public loadKinder(teilnahmenummer: string): void {

		if (this.loggingOut) {
			return;
		}

		const user = JSON.parse(localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER));
		const teilnahmeart: Teilnahmeart = user.rolle === 'PRIVAT' ? 'PRIVAT' : 'SCHULE';

		const teilnahmeIdentifier: TeilnahmeIdentifier = {
			jahr: this.wettbewerb.jahr,
			teilnahmeart: teilnahmeart,
			teilnahmenummer: teilnahmenummer
		}

		this.store.dispatch(KinderActions.teilnahmenummerInitialized({ teilnahmeIdentifier: teilnahmeIdentifier }));
		this.store.dispatch(KinderActions.startLoading());

		this.kinderService.loadKinder(teilnahmenummer).subscribe(
			kinder => this.store.dispatch(KinderActions.allKinderLoaded({ kinder: kinder })),
			(error => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public pruefeDuplikat(uuid: string, editorModel: KindEditorModel): void {

		this.store.dispatch(KinderActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel) as PrivatkindRequestData;

		this.kinderService.checkDuplikat(data).subscribe(
			warnung => this.store.dispatch(KinderActions.duplikatGeprueft({ duplikatwarnung: warnung })),
			(error => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public insertKind(uuid: string, editorModel: KindEditorModel): void {

		this.store.dispatch(KinderActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel) as PrivatkindRequestData;

		this.kinderService.insertKind(data).subscribe(
			responsePayload => this.store.dispatch(KinderActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message })),
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);
	}

	public updateKind(uuid: string, editorModel: KindEditorModel): void {

		this.store.dispatch(KinderActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel) as PrivatkindRequestData;

		this.kinderService.updateKind(data).subscribe(
			responsePayload => this.store.dispatch(KinderActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message })),
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);

	}

	public deleteKind(uuid: string): void {

		this.store.dispatch(KinderActions.startLoading());

		this.kinderService.deleteKind(uuid).subscribe(
			responsePayload => {

				this.store.dispatch(KinderActions.kindDeleted({ kind: responsePayload.data, outcome: responsePayload.message }));
				this.messageService.showMessage(responsePayload.message);
			},
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);

	}

	public resetState(): void {
		this.store.dispatch(KinderActions.resetModule());
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



