import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as LehrerSelectors from './+state/lehrer.selectors';
import { SchulenService } from '../services/schulen.service';
import * as LehrerActions from './+state/lehrer.actions';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Schule } from './schulen/schulen.model';
import { VeranstalterService } from '../services/veranstalter.service';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { Schulteilnahme } from '../wettbewerb/wettbewerb.model';
import { Message, MessageService } from '@minikaenguru-ws/common-messages';
import { User, AuthService } from '@minikaenguru-ws/common-auth';
import { take } from 'rxjs/operators';
import * as WettbewerbActions from '../wettbewerb/+state/wettbewerb.actions';
import { SchulkatalogFacade, KatalogItem } from '@minikaenguru-ws/common-schulkatalog';
import { Router } from '@angular/router';


@Injectable({ providedIn: 'root' })
export class LehrerFacade {


	public schulen$ = this.appStore.select(LehrerSelectors.alleSchulen);
	public selectedSchule$ = this.appStore.select(LehrerSelectors.selectedSchule);
	public schuleDetails$ = this.appStore.select(LehrerSelectors.schuleDetails);
	public lehrer$ = this.appStore.select(LehrerSelectors.lehrer);
	public showSchulkatalog$ = this.appStore.select(LehrerSelectors.showSchulkatalog);
	public showTextSchuleBereitsZugeordnet$ = this.appStore.select(LehrerSelectors.showTextSchuleBereitsZugeordnet);
	public btnAddMeToSchuleDisabled$ = this.appStore.select(LehrerSelectors.btnAddMeToSchuleDisabled);
	public loading$ = this.appStore.select(LehrerSelectors.loading);

	public alleSchulenDesLehrers$ = this.appStore.select(LehrerSelectors.alleSchulen);



	private loggingOut: boolean;

	constructor(private appStore: Store<AppState>,
		private veranstalterService: VeranstalterService,
		private authService: AuthService,
		private schulenService: SchulenService,
		private teilnahmenService: TeilnahmenService,
		private router: Router,
		private messageService: MessageService,
		private schulkatalogFacade: SchulkatalogFacade,
		private errorHandler: GlobalErrorHandlerService) {

		this.authService.onLoggingOut$.subscribe(
			loggingOut => this.loggingOut = loggingOut
		);
	}

	public loadLehrer(): void {

		if (this.loggingOut) {
			return;
		}

		this.appStore.dispatch(LehrerActions.startLoading());

		this.veranstalterService.getLehrer().pipe(
			take(1)
		).subscribe(
			data => {
				this.appStore.dispatch(LehrerActions.datenLehrerGeladen({ lehrer: data }));
				this.appStore.dispatch(WettbewerbActions.veranstalterLoaded({ veranstalter: data }));
			},
			(error => {
				this.errorHandler.handleError(error);
			})
		);
	}

	public loadSchulen(): void {

		if (this.loggingOut) {
			return;
		}

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

		if (this.loggingOut) {
			return;
		}

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
				this.errorHandler.handleError(error);
			})
		);
	}
	public changeAboNewsletter(): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.veranstalterService.toggleAboNewsletter().pipe(
			take(1)
		).subscribe(
			message => {
				this.appStore.dispatch(LehrerActions.aboNewsletterChanged());
				this.messageService.showMessage(message);
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}

	public selectSchule(schule: Schule): void {

		this.appStore.dispatch(LehrerActions.selectSchule({ schule: schule }));
	}

	public removeSchule(schule: Schule): void {

		this.appStore.dispatch(LehrerActions.startLoading());

		this.veranstalterService.removeSchule(schule.kuerzel).subscribe(
			message => {
				this.appStore.dispatch(LehrerActions.schuleRemoved({ kuerzel: schule.kuerzel }));
				this.messageService.showMessage(message);
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);

	}

	public neueSchuleSelected(item: KatalogItem): void {

		this.appStore.dispatch(LehrerActions.neueSchuleSelected({ selectedKatalogItem: item }));
	}

	public neueSchulsuche(): void {

		this.schulkatalogFacade.initSchulkatalog('ORT');
		this.appStore.dispatch(LehrerActions.schulkatalogEinblenden());
	}

	public closeSchulsuche(): void {
		this.appStore.dispatch(LehrerActions.closeSchulsuche());

	}

	public schuleHinzufuegen(katalogItem: KatalogItem): void {
		this.appStore.dispatch(LehrerActions.startLoading());

		this.veranstalterService.addSchule(katalogItem.kuerzel).pipe(
			take(1)
		).subscribe(
			responsePayload => {
				this.appStore.dispatch(LehrerActions.schuleAdded({ schule: responsePayload.data }));
				this.messageService.showMessage(responsePayload.message);
				this.router.navigateByUrl('/lehrer/schulen');
			},
			(error => {
				this.appStore.dispatch(LehrerActions.finishedWithError());
				this.errorHandler.handleError(error);
			})
		);
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
