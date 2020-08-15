import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { RegistrationState } from './+state/registration.reducer';
import { selectRegistrationMode, selectNewsletterAboState, selectShowSchulkatalog, selectShowSuccessDialog, selectSubmitStatus, selectSuccessDialogContent } from './+state/registration.selectors';
import * as RegistrationActions from './+state/registration.actions';
import { SchulkatalogFacade } from '@minikaenguru-ws/common-schulkatalog';
import { AuthService } from '@minikaenguru-ws/common-auth';

@Injectable({ providedIn: 'root' })
export class RegistrationFacade {

	public registrationMode$ = this.store.select(selectRegistrationMode);

	public newsletterAboState$ = this.store.select(selectNewsletterAboState);

	public showSchulkatalog$ = this.store.select(selectShowSchulkatalog);

	public showSuccessDialog$ = this.store.select(selectShowSuccessDialog);

	public submitState$ = this.store.select(selectSubmitStatus);

	public registrationSuccessMessage$ = this.store.select(selectSuccessDialogContent);


	constructor(private store: Store<RegistrationState>
		, private schulkatalogFacade: SchulkatalogFacade
		, private authService: AuthService) { }

	public setNewsletterAboState(value: boolean) {

		this.store.dispatch(RegistrationActions.newsletterAbonierenChanged({ flag: value }));

	}

	public resetRegistrationState() {
		this.store.dispatch(RegistrationActions.resetRegistrationState());
	}

	public activateModusLehrerkonto() {
		this.store.dispatch(RegistrationActions.registrationModeChanged({ mode: 'LEHRER' }));
		this.schulkatalogFacade.initSchulkatalog('ORT');
	}

	public privatkontoAnlegen(newsletterAbonnieren: boolean) {

		this.store.dispatch(RegistrationActions.registrationModeChanged({mode: 'PRIVAT'}));
		this.authService.privatkontoAnlegen(newsletterAbonnieren);

	}

	public lehrerkontoAnlegen(schulkuerzel: string, newsletterAbonnieren: boolean) {
		this.authService.lehrerkontoAnlegen(schulkuerzel, newsletterAbonnieren);
	}

	public schuleSelected(schulkuerzel: string) {
		this.store.dispatch(RegistrationActions.schuleSelected({ schulkuerzel: schulkuerzel }));
	}

	public resetSchulsuche() {
		this.store.dispatch(RegistrationActions.resetSchulsuche());
	}


}
