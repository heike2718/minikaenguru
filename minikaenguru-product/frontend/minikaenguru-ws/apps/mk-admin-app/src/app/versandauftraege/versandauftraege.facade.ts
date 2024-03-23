import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import * as VersandauftraegeActions from './+state/versandauftraege.actions';
import * as VersandauftraegeSelectors from './+state/versandauftraege.selectors';
import { Observable } from 'rxjs';
import { NewsletterVersandauftrag, Versandauftrag } from '../shared/newsletter-versandauftrage.model';
import { VersandauftragService } from './versandauftraege.service';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { MessageService } from '@minikaenguru-ws/common-messages';



@Injectable({
	providedIn: 'root'
})
export class VersandauftraegeFacade {


	versandauftraege$: Observable<Versandauftrag[]> = this.store.select(VersandauftraegeSelectors.versandauftraege);
	selectedVersandauftrag$: Observable<Versandauftrag | undefined> = this.store.select(VersandauftraegeSelectors.selectedVersandauftrag);

	constructor(private store: Store<AppState>,
		private versandauftraegeService: VersandauftragService,
		private errorHandler: GlobalErrorHandlerService,
		private messageService: MessageService,
	) { }

	

	public loadVersandauftraege(): void {

		this.store.dispatch(VersandauftraegeActions.startBackendCall());

		this.versandauftraegeService.loadVersandauftraege().subscribe(

			versandauftraege => {
				this.store.dispatch(VersandauftraegeActions.versandauftraegeLoaded({ versandauftraege: versandauftraege }));

			},
			(error => {
				this.store.dispatch(VersandauftraegeActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})
		);
	}



	public scheduleMailversand(auftrag: NewsletterVersandauftrag): void {

		this.versandauftraegeService.scheduleMailversand(auftrag).subscribe(

			responsePayload => {			

				if (responsePayload.data) {
					const versandauftrag = responsePayload.data;
					this.messageService.showMessage(responsePayload.message);
				}				
			},
			(error => {
				this.store.dispatch(VersandauftraegeActions.backendCallFinishedWithError());
				this.errorHandler.handleError(error);
			})

		);
	}	
};
