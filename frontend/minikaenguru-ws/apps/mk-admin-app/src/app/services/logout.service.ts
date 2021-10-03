import { AuthService, STORAGE_KEY_INVALID_SESSION } from '@minikaenguru-ws/common-auth';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { resetWettbewerbe } from '../wettbewerbe/+state/wettbewerbe.actions';
import { resetKataloge } from '../katalogpflege/+state/katalogpflege.actions';
import { resetVeranstalters } from '../veranstalter/+state/veranstalter.actions';
import { resetAktuelleMeldung } from '../aktuelle-meldung/+state/aktuelle-meldung.actions';
import { resetSchulteilnahmen } from '../schulteilnahmen/+state/schulteilnahmen.actions';
import { resetNewsletters } from '../newsletter/+state/newsletter.actions';
import { dateCleared } from '../eventlog/+state/eventlog.actions';
import { NewsletterFacade } from '../newsletter/newsletter.facade';


@Injectable({
	providedIn: 'root'
})
export class LogoutService {

	constructor(private authService: AuthService
		, private appStore: Store<AppState>, private newsletterFacade: NewsletterFacade) { }


	logout(): void {
		this.newsletterFacade.stopPollVersandinfo();
		this.authService.logOut(false);
		this.appStore.dispatch(resetWettbewerbe());
		this.appStore.dispatch(resetKataloge());
		this.appStore.dispatch(resetVeranstalters());
		this.appStore.dispatch(resetAktuelleMeldung());
		this.appStore.dispatch(resetSchulteilnahmen());
		this.appStore.dispatch(dateCleared());
		this.appStore.dispatch(resetNewsletters());

		localStorage.removeItem(STORAGE_KEY_INVALID_SESSION);
	}
}
