import { Injectable } from '@angular/core';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as WettbewerbActions from './+state/wettbewerb.actions';
import { aktuellerWettbewerb, veranstalter } from './+state/wettbewerb.selectors';
import { tap, take } from 'rxjs/operators';
import { Wettbewerb } from './wettbewerb.model';

const WETTBEWERB_STORAGE_KEY = 'mkv_wettbewerb';

@Injectable({ providedIn: 'root' })
export class WettbewerbFacade {

	public aktuellerWettbewerb$ = this.appStore.select(aktuellerWettbewerb);

	public veranstalter$ = this.appStore.select(veranstalter);

	constructor(private appStore: Store<AppState>,
		private teilnahmenService: TeilnahmenService,
		private errorHandler: GlobalErrorHandlerService) { }


	public ladeAktuellenWettbewerb(): void {

		// I0419: aktueller Wettbewerb muss immer neu geladen werden!
		this.aktuellerWettbewerb$.pipe(
			tap(
				wettbewerb => {
					if (!wettbewerb) {
						this.teilnahmenService.getAktuellenWettbewerb().pipe(
							take(1)
						).subscribe(
							wb => {
								localStorage.setItem(WETTBEWERB_STORAGE_KEY, JSON.stringify(wb));
								this.appStore.dispatch(WettbewerbActions.aktuellerWettbewerbGeladen({ wettbewerb: wb }))
							},
							(error => {
								this.errorHandler.handleError(error);
							})
						)
					}
				}
			)
		).subscribe();
	}

	public resetState(): void {
		localStorage.removeItem(WETTBEWERB_STORAGE_KEY);
		this.appStore.dispatch(WettbewerbActions.reset());
	}

}
