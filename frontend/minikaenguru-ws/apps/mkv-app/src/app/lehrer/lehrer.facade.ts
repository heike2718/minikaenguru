import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { alleSchulen, selectedSchule, loading, schuleDetails, hatZugangZuUnterlagen } from './+state/lehrer.selectors';
import { SchulenService } from '../services/schulen.service';
import * as LehrerActions from './+state/lehrer.actions';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Schule } from './schulen/schulen.model';
import { VeranstalterService } from '../services/veranstalter.service';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { Schulteilnahme } from '../wettbewerb/wettbewerb.model';
import { Message, MessageService } from '@minikaenguru-ws/common-messages';
import { User } from '@minikaenguru-ws/common-auth';
import { first, take } from 'rxjs/operators';


@Injectable({ providedIn: 'root' })
export class LehrerFacade {


	public hatZugangZuUnterlagen$ = this.appStore.select(hatZugangZuUnterlagen);

	public schulen$ = this.appStore.select(alleSchulen);
	public selectedSchule$ = this.appStore.select(selectedSchule);
	public schuleDetails$ = this.appStore.select(schuleDetails);

	public loading$ = this.appStore.select(loading);

	constructor(private appStore: Store<AppState>,
		private veranstalterService: VeranstalterService,
		private schulenService: SchulenService,
		private teilnahmenService: TeilnahmenService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService) {
	}

	public ladeZugangUnterlagen(): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.veranstalterService.getZugangsstatusUnterlagen().pipe(
			take(1)
		).subscribe(
			zugang => {
				this.appStore.dispatch(LehrerActions.zugangsstatusUnterlagenGeladen({ hatZugang: zugang }));
			},
			(error => {
				this.errorHandler.handleError(error);
			})
		);
	}

	public loadSchulen(): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.schulenService.findSchulen().pipe(
			take(1)
		).subscribe(
			schulen => {
				this.appStore.dispatch(LehrerActions.schulenLoaded({ schulen: schulen }));
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}


	public loadDetails(schulkuerzel: string): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.schulenService.loadDetails(schulkuerzel).pipe(
			take(1)
		).subscribe(
			data => {
				this.appStore.dispatch(LehrerActions.schuleDetailsLoaded({ schule: data }))
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public schuleAnmelden(schule: Schule, user: User): void {

		this.teilnahmenService.schuleAnmelden(schule).pipe(
			take(1)
		).subscribe(
			responsePayload => {

				const teilnahme = <Schulteilnahme>responsePayload.data;
				const message = <Message>responsePayload.message;

				this.messageService.info(message.message);

				this.appStore.dispatch(LehrerActions.schuleAngemeldet({ teilnahme: teilnahme, angemeldetDurch: user.fullName }));
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error)
			})
		);
	}

	public selectSchule(schule: Schule): void {

		this.appStore.dispatch(LehrerActions.selectSchule({ schule: schule }));
	}

	public restoreDetailsFromCache(schulkuerzel: string): void {
		this.appStore.dispatch(LehrerActions.restoreDetailsFromCache({ kuerzel: schulkuerzel }));
	}

	public resetSelection(): void {
		this.appStore.dispatch(LehrerActions.deselectSchule());
	}

	public resetState(): void {

		this.appStore.dispatch(LehrerActions.resetLehrer());
	}

}
