import { AuthService } from '@minikaenguru-ws/common-auth';
import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { resetWettbewerbe } from '../wettbewerbe/+state/wettbewerbe.actions';
import { resetKataloge } from '../katalogpflege/+state/katalogpflege.actions';
import { resetVeranstalters } from '../veranstalter/+state/veranstalter.actions';
import { resetAktuelleMeldung } from '../aktuelle-meldung/+state/aktuelle-meldung.actions';
import { resetSchulteilnahmen } from '../schulteilnahmen/+state/schulteilnahmen.actions';
import { dateCleared } from '../eventlog/+state/eventlog.actions';


@Injectable({
	providedIn: 'root'
})
export class LogoutService {

	constructor(private authService: AuthService
		, private appStore: Store<AppState>) { }


	logout(): void {
		this.authService.logout();
		this.appStore.dispatch(resetWettbewerbe());
		this.appStore.dispatch(resetKataloge());
		this.appStore.dispatch(resetVeranstalters());
		this.appStore.dispatch(resetAktuelleMeldung());
		this.appStore.dispatch(resetSchulteilnahmen());
		this.appStore.dispatch(dateCleared());
	}
}
