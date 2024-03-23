import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { RegistrationState } from './+state/registration.reducer';
import * as RegistrationSelectors from './+state/registration.selectors';
import * as RegistrationActions from './+state/registration.actions';
import { SchulkatalogFacade } from '@minikaenguru-ws/common-schulkatalog';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { Observable } from 'rxjs';
import { RegistrationMode } from './domain/entities';

@Injectable({ providedIn: 'root' })
export class RegistrationFacade {

	public registrationReady$: Observable<boolean> = this.store.select(RegistrationSelectors.registrationFormReady);

	public registrationMode$: Observable< RegistrationMode | undefined> = this.store.select(RegistrationSelectors.selectRegistrationMode);

	public newsletterAboState$: Observable<boolean> = this.store.select(RegistrationSelectors.selectNewsletterAboState);

	public showSchulkatalog$: Observable<boolean> = this.store.select(RegistrationSelectors.selectShowSchulkatalog);

	public showSuccessDialog$: Observable<boolean> = this.store.select(RegistrationSelectors.selectShowSuccessDialog);

	public submitState$: Observable<boolean> = this.store.select(RegistrationSelectors.selectSubmitStatus);

	public registrationSuccessMessage$: Observable<string | undefined> = this.store.select(RegistrationSelectors.selectSuccessDialogContent);


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
