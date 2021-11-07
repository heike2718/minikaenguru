import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as KinderSelectors from './+state/kinder.selectors';
import * as KinderActions from './+state/kinder.actions';
import * as KlassenSelectors from '../klassen/+state/klassen.selectors';
import * as KlassenActions from '../klassen/+state/klassen.actions';
import * as LoesungszettelActions from '../loesungszettel/+state/loesungszettel.actions';
import { Observable } from 'rxjs';
import {
	Kind
	, KindEditorModel
	, Duplikatwarnung
	, KindRequestData
	, TeilnahmeIdentifierAktuellerWettbewerb
	, Teilnahmeart
} from '@minikaenguru-ws/common-components';
import { AuthService, STORAGE_KEY_USER } from '@minikaenguru-ws/common-auth';
import { KinderService } from './kinder.service';
import { map, withLatestFrom, filter } from 'rxjs/operators';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { KinderMap, KlassenwechselDaten } from './kinder.model';
import { Message, MessageService } from '@minikaenguru-ws/common-messages';
import { environment } from '../../environments/environment';
import { Schule } from '../lehrer/schulen/schulen.model';
import { Router } from '@angular/router';
import { LogService } from '@minikaenguru-ws/common-logging';



@Injectable({
	providedIn: 'root'
})
export class KinderFacade {

	public teilnahmeIdentifier$: Observable<TeilnahmeIdentifierAktuellerWettbewerb | undefined> = this.store.select(KinderSelectors.teilnahmeIdentifier);
	public kindEditorModel$: Observable<KindEditorModel> = this.store.select(KinderSelectors.kindEditorModel);
	public kinder$: Observable<Kind[]>;
	public kinderGeladen$: Observable<boolean> = this.store.select(KinderSelectors.kinderGeladen);
	public anzahlKinder$: Observable<number>;
	public duplikatwarnung$: Observable<Duplikatwarnung | undefined> = this.store.select(KinderSelectors.duplikatwarnung);
	public saveOutcome$: Observable<Message | undefined> = this.store.select(KinderSelectors.saveOutcome);

	// wird in startKlassenwechsel() initialisiert und ist daher nicht undefined!
	public klassenwechselDaten$!: Observable<KlassenwechselDaten>;
	public selectedKind$: Observable<Kind | undefined> = this.store.select(KinderSelectors.selectedKind);

	private loggingOut: boolean = false;

	constructor(private store: Store<AppState>,
		private authService: AuthService,
		private kinderService: KinderService,
		private messageService: MessageService,
		private logger: LogService,
		private errorHandler: GlobalErrorHandlerService,
		private router: Router) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);

		this.kinder$ = this.getKinder();
		this.anzahlKinder$ = this.getAnzahlKinder();
	}

	public createNewKind(klasseUuid?: string): void {

		this.store.dispatch(KinderActions.createNewKind({ klasseUuid: klasseUuid }));
		this.kinder$ = this.getKinder();

	}

	public selectKind(kind: Kind): void {
		this.store.dispatch(KinderActions.selectKind({kind: kind}));
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

		const userObj = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);
		
		if (!userObj) {
			this.logger.error('there is no user in localTorage');
			return;
		}

		const user = JSON.parse(userObj);
		const teilnahmeart: Teilnahmeart = user.rolle === 'PRIVAT' ? 'PRIVAT' : 'SCHULE';

		const teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb = {
			teilnahmeart: teilnahmeart,
			teilnahmenummer: teilnahmenummer
		}

		this.store.dispatch(KinderActions.teilnahmenummerInitialized({ teilnahmeIdentifier: teilnahmeIdentifier }));
		this.store.dispatch(KinderActions.startLoading());

		this.kinderService.loadKinder(teilnahmenummer).subscribe(
			kinder => {
				this.store.dispatch(KinderActions.allKinderLoaded({ kinder: kinder }));
				this.kinder$ = this.getKinder();
				this.anzahlKinder$ = this.getAnzahlKinder();
			},
			(error => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public pruefeDuplikat(uuid: string, editorModel: KindEditorModel): void {

		if (!uuid) {
			return;
		}

		this.store.dispatch(KinderActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel, undefined) as KindRequestData;

		this.kinderService.checkDuplikat(data).subscribe(
			warnung => this.store.dispatch(KinderActions.duplikatGeprueft({ duplikatwarnung: warnung })),
			(error => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public insertKind(uuid: string, editorModel: KindEditorModel, schule?: Schule): void {

		this.store.dispatch(KinderActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel, schule) as KindRequestData;

		this.kinderService.insertKind(data).subscribe(
			responsePayload => {
				this.store.dispatch(KinderActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message }));
				if (schule) {
					this.store.dispatch(KlassenActions.kindAdded());
				}
			},
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);
	}

	public updateKind(uuid: string, editorModel: KindEditorModel, schule?: Schule): void {

		this.store.dispatch(KinderActions.startLoading());

		const data = this.mapFromEditorModel(uuid, editorModel, schule) as KindRequestData;

		this.kinderService.updateKind(data).subscribe(
			responsePayload => {
				this.store.dispatch(KinderActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message }));
			},
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);

	}

	public markKlassenstufeKorrekt(selectedKind: Kind, schule?: Schule): void {

		this.store.dispatch(KinderActions.startLoading());

		const kindEditorModel: KindEditorModel = {
			vorname: selectedKind.vorname,
			nachname: selectedKind.nachname ? selectedKind.nachname : '',
			zusatz: selectedKind.zusatz ? selectedKind.zusatz : '',
			klassenstufe: selectedKind.klassenstufe,
			sprache: selectedKind.sprache
		};

		const data: KindRequestData = this.mapFromEditorModel(selectedKind.uuid, kindEditorModel, schule);

		this.kinderService.updateKind(data).subscribe(
			responsePayload => {
				this.store.dispatch(KinderActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message }));
			},
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);
	}

	public moveKind(kind: Kind, editorModel: KindEditorModel, schule: Schule): void {

		if (!kind.klasseId || !editorModel.klasseUuid) {
			this.logger.error('Klassenwechsel nicht möglich - brechen ab: kind.klasseId=' + kind.klasseId + ", editorModel=" + editorModel.klasseUuid);
			return;
		}

		const kindKlasseUUID: string = kind.klasseId;
		const ediorModelKlasseUUID: string = editorModel.klasseUuid;

		this.store.dispatch(KinderActions.startLoading());

		const data = this.mapFromEditorModel(kind.uuid, editorModel, schule) as KindRequestData;

		this.kinderService.updateKind(data).subscribe(
			responsePayload => {
				this.store.dispatch(KinderActions.kindSaved({ kind: responsePayload.data, outcome: responsePayload.message }));
				this.store.dispatch(KlassenActions.kindMoved({kind: kind, sourceKlasseUuid: kindKlasseUUID, targetKlasseUuid: ediorModelKlasseUUID}));
				this.kinder$ = this.getKinder();
			},
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);

	}

	public deleteKind(kind: Kind, klasseUuid: string): void {

		this.store.dispatch(KinderActions.startLoading());

		this.kinderService.deleteKind(kind.uuid).subscribe(
			responsePayload => {

				this.store.dispatch(KinderActions.kindDeleted({ kind: responsePayload.data, outcome: responsePayload.message }));
				if (klasseUuid) {
					this.store.dispatch(KlassenActions.kindDeleted({kind: kind}));
				}
			    this.store.dispatch(LoesungszettelActions.kindDeleted({kindUuid: kind.uuid}));
				this.kinder$ = this.getKinder();
				this.messageService.showMessage(responsePayload.message);
			},
			(error) => {
				this.store.dispatch(KinderActions.finishedWithError());
				this.errorHandler.handleError(error);
			}
		);

	}

	public startKlassenwechsel(kind: Kind): void {
		this.klassenwechselDaten$ = this.getKlassenwechselDaten(kind);
		this.router.navigateByUrl('/kind/klassenwechsel');
	}

	public resetState(): void {
		this.store.dispatch(KinderActions.resetModule());
	}


	// ////////////////////////////////////// private members //////////////////

	private getAnzahlKinder(): Observable<number> {

		return this.getKinder().pipe(
			map(kinder => kinder.length)
		);

	}

	private getKinder(): Observable<Kind[]> {

		const a$ = this.store.select(KlassenSelectors.selectedKlasse);
		const b$ = this.store.select(KinderSelectors.kinderMap);

		return b$.pipe(
			withLatestFrom(a$)
		).pipe(
			map(x => new KinderMap(x[0]).filterWithKlasse(x[1]))
		);

	}

	private getKlassenwechselDaten(kind: Kind): Observable<KlassenwechselDaten> {

		const kl$ = this.store.select(KlassenSelectors.klassen);

		return kl$.pipe(
			map(alle => alle.filter(kl => kl.uuid !== kind.klasseId)),
			map(filteredKlassen => <KlassenwechselDaten> {kind: kind,zielklassen: filteredKlassen})
		);
	}

	private mapFromEditorModel(uuid: string, editorModel: KindEditorModel, schule?: Schule): KindRequestData {

		let data: KindRequestData = {
			uuid: uuid,
			kind: editorModel
		};

		if (schule) {
			data = { ...data, kuerzelLand: schule.kuerzelLand };
		}

		return data
	}
};



