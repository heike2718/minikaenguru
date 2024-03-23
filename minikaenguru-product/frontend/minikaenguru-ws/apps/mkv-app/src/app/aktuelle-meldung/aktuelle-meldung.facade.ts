import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { AktuelleMeldungService } from './aktuelle-meldung.service';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { aktuelleMeldung, aktuelleMeldungGeladen, habenAktuelleMeldung } from './+state/aktuelle-meldung.selectors';
import * as AktuelleMeldungActions from './+state/aktuelle-meldung.actions';
import { first } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AktuelleMeldungFacade {

	public aktuelleMeldung$ = this.appStore.select(aktuelleMeldung);
	public aktuelleMeldungGeladen$ = this.appStore.select(aktuelleMeldungGeladen);
	public aktuelleMeldungNichtLeer$ = this.appStore.select(habenAktuelleMeldung);


	constructor(private appStore: Store<AppState>,
		private aktuelleMeldungService: AktuelleMeldungService,
		private errorHandler: GlobalErrorHandlerService) {

	}


	public ladeAktuelleMeldung(): void {

		this.aktuelleMeldungService.loadAktuelleMeldung().pipe(
			first()
		).subscribe(
			m => this.appStore.dispatch(AktuelleMeldungActions.aktuelleMeldungGeladen({ aktuelleMeldung: m })),
			(error => this.errorHandler.handleError(error))
		);
	}

	public resetState(): void {
		this.appStore.dispatch(AktuelleMeldungActions.aktuelleMeldungReset());
	}
}
