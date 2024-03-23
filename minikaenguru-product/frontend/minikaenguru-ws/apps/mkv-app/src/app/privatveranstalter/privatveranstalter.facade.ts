import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as PrivatveranstalterActions from './+state/privatveranstalter.actions';
import * as VeranstalterSelectors from './+state/privatveranstalter.selectors';
import { VeranstalterService } from '../services/veranstalter.service';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { MessageService, Message } from '@minikaenguru-ws/common-messages';
import { Privatteilnahme } from '../wettbewerb/wettbewerb.model';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { AuthService } from '@minikaenguru-ws/common-auth';
import * as WettbewerbActions from '../wettbewerb/+state/wettbewerb.actions';


@Injectable({ providedIn: 'root' })
export class PrivatveranstalterFacade {

	public veranstalter$ = this.appStore.select(VeranstalterSelectors.privatveranstalter);
	public aktuelleTeilnahmeGeladen$ = this.appStore.select(VeranstalterSelectors.aktuelleTeilnahmeGeladen);
	public hatZugangZuUnterlagen$ = this.appStore.select(VeranstalterSelectors.zugangUnterlagen);

	private loggingOut: boolean = false;

	constructor(private appStore: Store<AppState>,
		private authService: AuthService,
		private veranstalterService: VeranstalterService,
		private teilnahmenService: TeilnahmenService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService) {

			this.authService.onLoggingOut$.subscribe(
				loggingOut => this.loggingOut = loggingOut
			);
		}


	public resetState(): void {

		this.appStore.dispatch(PrivatveranstalterActions.resetPrivatveranstalter());

	}

	public loadInitialTeilnahmeinfos(): void {

		if (this.loggingOut) {
			return;
		}

		this.veranstalterService.loadPrivatveranstalter().pipe(
			take(1)
		).subscribe(
			veranstalter => {
				this.appStore.dispatch(PrivatveranstalterActions.privatveranstalterGeladen({ veranstalter: veranstalter }));
				this.appStore.dispatch(WettbewerbActions.veranstalterLoaded({veranstalter: veranstalter}));
			},
			(error => {
				this.appStore.dispatch(PrivatveranstalterActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public privatveranstalterAnmelden(): void {

		this.teilnahmenService.privatveranstalterAnmelden().pipe(
			take(1)
		).subscribe(
			responsePayload => {

				const teilnahme = <Privatteilnahme>responsePayload.data;
				const message = <Message>responsePayload.message;

				this.messageService.info(message.message);
				this.appStore.dispatch(PrivatveranstalterActions.privatveranstalterAngemeldet({ teilnahme: teilnahme }));

				this.reloadZugangsstatusUnterlagen();
			},
			(error => {
				this.appStore.dispatch(PrivatveranstalterActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public changeAboNewsletter(): void {

		this.veranstalterService.toggleAboNewsletter().pipe(
			take(1)
		).subscribe(
			message => {
				this.appStore.dispatch(PrivatveranstalterActions.aboNewsletterChanged());
				this.messageService.showMessage(message);
			},
			(error => {
				this.appStore.dispatch(PrivatveranstalterActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	// ///////////////////////////////////////

	private reloadZugangsstatusUnterlagen(): void {

		this.veranstalterService.loadPrivatveranstalter().subscribe(
			veranstalter => {
				this.appStore.dispatch(PrivatveranstalterActions.privatveranstalterGeladen({ veranstalter: veranstalter }));
			}
		)
	}

}
