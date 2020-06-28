import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as PrivatveranstalterActions from './+state/privatveranstalter.actions';
import { loading, privatveranstalterState } from './+state/privatveranstalter.selectors';
import { VeranstalterService } from '../services/veranstalter.service';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { MessageService, Message } from '@minikaenguru-ws/common-messages';
import { Privatteilnahme } from '../wettbewerb/wettbewerb.model';


@Injectable({ providedIn: 'root' })
export class PrivatveranstalterFacade {

	public loading$ = this.appStore.select(loading);
	public veranstalter$ = this.appStore.select(privatveranstalterState);

	constructor(private appStore: Store<AppState>,
		private veranstalterService: VeranstalterService,
		private teilnahmenService: TeilnahmenService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService) { }


	public resetState(): void {

		this.appStore.dispatch(PrivatveranstalterActions.resetPrivatveranstalter());

	}

	public loadInitialTeilnahmeinfos(): void {

		this.appStore.dispatch(PrivatveranstalterActions.startLoading());

		this.veranstalterService.loadPrivatveranstalter().subscribe(
			veranstalter => {
				this.appStore.dispatch(PrivatveranstalterActions.privatveranstalterGeladen({ veranstalter: veranstalter }));
			},
			(error => {
				this.appStore.dispatch(PrivatveranstalterActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public privatveranstalterAnmelden(): void {

		this.teilnahmenService.privatveranstalterAnmelden().subscribe(
			responsePayload => {

				const teilnahme = <Privatteilnahme>responsePayload.data;
				const message = <Message>responsePayload.message;

				this.messageService.info(message.message);
				this.appStore.dispatch(PrivatveranstalterActions.privatveranstalterAngemeldet({ teilnahme: teilnahme }));
			},
			(error => {
				this.appStore.dispatch(PrivatveranstalterActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

}
