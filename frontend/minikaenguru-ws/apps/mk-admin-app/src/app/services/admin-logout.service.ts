import { AuthService, STORAGE_KEY_INVALID_SESSION } from '@minikaenguru-ws/common-auth';
import { Injectable, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { resetVeranstalters } from '../veranstalter/+state/veranstalter.actions';
import { resetAktuelleMeldung } from '../aktuelle-meldung/+state/aktuelle-meldung.actions';
import { resetSchulteilnahmen } from '../schulteilnahmen/+state/schulteilnahmen.actions';
import { resetMustertexte } from '../mustertexte/+state/mustertexte.actions';
import { resetNewsletters } from '../newsletter/+state/newsletter.actions';
import { dateCleared } from '../eventlog/+state/eventlog.actions';
import { resetUploads } from '../uploads/+state/uploads.actions';
import { resetLoesungszettel } from '../loesungszettel/+state/loesungszettel.actions';
import { resetStatistiken } from '../statistik/+state/statistic.actions';
import { AdminWettbewerbFacade } from './admin-wettbewerb.facade';
import { AdminSchulkatalogFacade } from '@minikaenguru-ws/admin-schulkatalog';
import { resetVersandauftraege } from '../versandauftraege/+state/versandauftraege.actions';


@Injectable({
	providedIn: 'root'
})
export class AdminLogoutService {

	#adminKatalogFacade = inject(AdminSchulkatalogFacade);

	constructor(private authService: AuthService
		, private appStore: Store<AppState>, private aminWettbewerbFacade: AdminWettbewerbFacade) { }


	logout(): void {

		// I0419: status darf nicht im localStorage verbleiben!
		this.aminWettbewerbFacade.resetState();
		this.#adminKatalogFacade.onLogout();
		this.authService.logOut(false);
		this.appStore.dispatch(resetVeranstalters());
		this.appStore.dispatch(resetAktuelleMeldung());
		this.appStore.dispatch(resetSchulteilnahmen());
		this.appStore.dispatch(dateCleared());
		this.appStore.dispatch(resetMustertexte());
		this.appStore.dispatch(resetNewsletters());
		this.appStore.dispatch(resetVersandauftraege())
		this.appStore.dispatch(resetUploads());
		this.appStore.dispatch(resetLoesungszettel());
		this.appStore.dispatch(resetStatistiken());
		

		localStorage.removeItem(STORAGE_KEY_INVALID_SESSION);
	}
}
