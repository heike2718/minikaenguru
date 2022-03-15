import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { MessageService } from "@minikaenguru-ws/common-messages";
import { GlobalErrorHandlerService } from "../infrastructure/global-error-handler.service";
import { MustertexteService } from "./mustertexte.service";
import * as MustertexteActions from './+state/mustertexte.actions';
import * as MustertexteSelectors from './+state/mustertexte.selectors';
import { AppState } from "../reducers";
import { Store } from "@ngrx/store";
import { Mustertext } from "../shared/shared-entities.model";
import { Observable } from "rxjs";


@Injectable({
	providedIn: 'root'
})
export class MustertexteFacade {

	public mustertexte$: Observable<Mustertext[]> = this.store.select(MustertexteSelectors.mustertexte);
	public mustertexteLoaded$: Observable<boolean> = this.store.select(MustertexteSelectors.mustertexteLoaded);

    constructor(private mustertexteService: MustertexteService,
        private errorHandler: GlobalErrorHandlerService,
		private messageService: MessageService,
		private store: Store<AppState>,
		private router: Router) {}


	public loadMustertexte(): void {

		this.store.dispatch(MustertexteActions.startBackendCall());

		const mustertexte: Mustertext[] = [];

		mustertexte.push({
			kategorie: 'MAIL',
			name: 'erster Mustertext',
			uuid: 'uuid-1'
		});

		mustertexte.push({
			kategorie: 'NEWSLETTER',
			name: 'zweiter Mustertext',
			uuid: 'uuid-2'
		});

		this.store.dispatch(MustertexteActions.mustertexteLoaded({mustertexte: mustertexte}));

		/*
		this.mustertexteService.loadMustertexte().subscribe(
			mustertexte => {
				this.store.dispatch(MustertexteActions.mustertexteLoaded({ mustertexte: mustertexte }));

			},
			(error => {
				this.store.dispatch(MustertexteActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);
		*/
	}
};
