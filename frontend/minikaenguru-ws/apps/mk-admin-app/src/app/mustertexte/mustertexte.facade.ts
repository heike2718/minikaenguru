import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { MessageService, ResponsePayload } from "@minikaenguru-ws/common-messages";
import { GlobalErrorHandlerService } from "../infrastructure/global-error-handler.service";
import { MustertexteService } from "./mustertexte.service";
import * as MustertexteActions from './+state/mustertexte.actions';
import * as MustertexteSelectors from './+state/mustertexte.selectors';
import { AppState } from "../reducers";
import { Store } from "@ngrx/store";
import { Mustertext, MUSTRETEXT_KATEGORIE } from "../shared/shared-entities.model";
import { Observable, of } from "rxjs";


@Injectable({
	providedIn: 'root'
})
export class MustertexteFacade {

	public mustertexte$: Observable<Mustertext[]> = this.store.select(MustertexteSelectors.mustertexte);
	public mustertexteLoaded$: Observable<boolean> = this.store.select(MustertexteSelectors.mustertexteLoaded);
    public editorModel$: Observable<Mustertext | undefined> = this.store.select(MustertexteSelectors.mustertextEditoModel);


    constructor(private mustertexteService: MustertexteService,
        private errorHandler: GlobalErrorHandlerService,
		private messageService: MessageService,
		private store: Store<AppState>,
		private router: Router) {}


	public loadMustertexte(): void {

		this.store.dispatch(MustertexteActions.startBackendCall());

		this.mustertexteService.loadMustertexte().subscribe(
			mustertexte => {
				this.store.dispatch(MustertexteActions.mustertexteLoaded({ mustertexte: mustertexte }));

			},
			(error => {
				this.store.dispatch(MustertexteActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public loadMustertextDetails(selectedMustertext: Mustertext): void {

		if (selectedMustertext.text === undefined) {
			this.store.dispatch(MustertexteActions.startBackendCall());

			this.mustertexteService.loadMustertext(selectedMustertext.uuid).subscribe(
				m => {
					this.store.dispatch(MustertexteActions.mustertextDetailsLoaded({mustertext: m}));
				},
				(error => {
					this.store.dispatch(MustertexteActions.backendCallFinishedWithError());
					this.errorHandler.handleError(error);
				})
			);		
		} else {
			this.store.dispatch(MustertexteActions.mustertextDetailsLoaded({mustertext: selectedMustertext}));
		}
	}

	public createNewMustertext(): void {

		this.store.dispatch(MustertexteActions.createNewMustertext());
		this.router.navigateByUrl('/mustertext-editor/neu');

	}

	public startEditMustertext(mustertext: Mustertext): void {

		if (!mustertext.text) {

			this.mustertexteService.loadMustertext(mustertext.uuid).subscribe(
				m => {
					this.store.dispatch(MustertexteActions.mustertextDetailsLoaded({mustertext: m}));
					this.store.dispatch(MustertexteActions.editMustertextTriggered({mustertext: m}));
					this.router.navigateByUrl('/mustertext-editor/' + m.uuid);
				}
			);
		} else {
			this.store.dispatch(MustertexteActions.editMustertextTriggered({mustertext: mustertext}));
			this.router.navigateByUrl('/mustertext-editor/' + mustertext.uuid);
		}		
    }	

	public cancelEditMustertext(): void {
		this.store.dispatch(MustertexteActions.editCanceled());
	}

	public selectFilter(filter: MUSTRETEXT_KATEGORIE): void {
		this.store.dispatch(MustertexteActions.filterkriteriumChanged({neuerFilter: filter}));
	}

	public saveMustertext(mustertext: Mustertext): void {

		this.store.dispatch(MustertexteActions.startBackendCall());

		const cachedMustertext = {...mustertext};

		let observable: Observable<ResponsePayload>;

		if (mustertext.uuid === 'neu') {
			observable = this.mustertexteService.insertMustertext(mustertext);
		} else {
			observable = this.mustertexteService.updateMustertext(mustertext);
		}

		observable.subscribe(
			responsePaylaod => {

				if (responsePaylaod.message.level === 'INFO') {
					this.store.dispatch(MustertexteActions.mustertextSaved({mustertext: responsePaylaod.data}));
				} else {
					this.store.dispatch(MustertexteActions.backendCallFinishedWithError());
				}

				if (mustertext.uuid === 'neu') {
					this.store.dispatch(MustertexteActions.mustertextDeleted({mustertext: cachedMustertext}));
				}

				this.messageService.showMessage(responsePaylaod.message);

			},
			(error => {
				this.store.dispatch(MustertexteActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})

		);

		this.store.dispatch(MustertexteActions.mustertextSaved({mustertext: mustertext}));

	}

	public deleteMustertext(mustertext: Mustertext): void {

		this.store.dispatch(MustertexteActions.startBackendCall());

		this.mustertexteService.deleteMustertext(mustertext).subscribe(
			responsePaylaod => {

				if (responsePaylaod.message.level === 'INFO') {
					this.store.dispatch(MustertexteActions.mustertextDeleted({mustertext: mustertext}));
				} else {
					this.store.dispatch(MustertexteActions.backendCallFinishedWithError());
				}

				this.messageService.showMessage(responsePaylaod.message);

			},
			(error => {
				this.store.dispatch(MustertexteActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}


	/*
	private loadDetails(mustertext: Mustertext): Observable<Mustertext> {

		const vollstaendigerMustertext: Mustertext = {...mustertext, text: 'bitte verwenden Sie die Bla-Funktion, wenn Sie nicht weiterkommen'};

		return of(vollstaendigerMustertext);
	}
	*/
};

