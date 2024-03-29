import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { AktuelleMeldungService } from './aktuelle-meldung.service';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { aktuelleMeldung, aktuelleMeldungGeladen, habenAktuelleMeldung } from './+state/aktuelle-meldung.selectors';
import * as AktuelleMeldungActions from './+state/aktuelle-meldung.actions';
import { AktuelleMeldung } from './aktuelle-meldung.model';
import { ResponsePayload, MessageService } from '@minikaenguru-ws/common-messages';
import { AuthService } from '@minikaenguru-ws/common-auth';

@Injectable({ providedIn: 'root' })
export class AktuelleMeldungFacade {

	public aktuelleMeldung$ = this.appStore.select(aktuelleMeldung);
	public aktuelleMeldungGeladen$ = this.appStore.select(aktuelleMeldungGeladen);
	public aktuelleMeldungNichtLeer$ = this.appStore.select(habenAktuelleMeldung);

	private loggingOut: boolean = false;

	constructor(private appStore: Store<AppState>,
		private authService: AuthService,
		private aktuelleMeldungService: AktuelleMeldungService,
		private messageService: MessageService,
		private errorHandler: GlobalErrorHandlerService) {

			this.authService.onLoggingOut$.subscribe(
				loggingOut => this.loggingOut = loggingOut
			);

	}


	public ladeAktuelleMeldung(): void {

		if (this.loggingOut) {
			return;
		}

		this.aktuelleMeldungService.loadAktuelleMeldung().subscribe(

			m => this.appStore.dispatch(AktuelleMeldungActions.aktuelleMeldungGeladen({ 'aktuelleMeldung': m })),
			(error => this.errorHandler.handleError(error))
		);
	}

	public aktuelleMeldungSpeichern(meldung: AktuelleMeldung): void {

		this.aktuelleMeldungService.saveAktuelleMeldung(meldung).subscribe(

			(reponsePayload: ResponsePayload) => {
				this.messageService.showMessage(reponsePayload.message);
				this.appStore.dispatch(AktuelleMeldungActions.aktuelleMeldungGespeichert({ 'aktuelleMeldung': meldung }));
			},
			(error => this.errorHandler.handleError(error))
		);

	}

	public aktuelleMeldungLoeschen(): void {

		this.aktuelleMeldungService.deleteAktuelleMeldung().subscribe(

			(reponsePayload: ResponsePayload) => {
				this.messageService.showMessage(reponsePayload.message);
				this.appStore.dispatch(AktuelleMeldungActions.aktuelleMeldungGeloescht());
			},
			(error => this.errorHandler.handleError(error))
		);

	}
}
