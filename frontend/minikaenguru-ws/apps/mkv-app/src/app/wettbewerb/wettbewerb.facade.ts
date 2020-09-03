import { Injectable } from '@angular/core';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as WettbewerbActions from './+state/wettbewerb.actions';
import { aktuellerWettbewerb } from './+state/wettbewerb.selectors';
import { tap, first } from 'rxjs/operators';
import { Wettbewerb } from './wettbewerb.model';

const WETTBEWERB_STORAGE_KEY = 'mkv_wettbewerb';

@Injectable({ providedIn: 'root' })
export class WettbewerbFacade {

	public aktuellerWettbewerb$ = this.appStore.select(aktuellerWettbewerb);

	constructor(private appStore: Store<AppState>,
		private teilnahmenService: TeilnahmenService,
		private errorHandler: GlobalErrorHandlerService) { }


	public ladeAktuellenWettbewerb(): void {

		const w = localStorage.getItem(WETTBEWERB_STORAGE_KEY);
		if (w) {
			const wettbewerb: Wettbewerb = JSON.parse(w);
			this.appStore.dispatch(WettbewerbActions.aktuellerWettbewerbGeladen({ wettbewerb: wettbewerb }))

		} else {

			this.aktuellerWettbewerb$.pipe(
				tap(
					wettbewerb => {
						if (!wettbewerb) {
							this.teilnahmenService.getAktuellenWettbewerb().subscribe(
								w => {
									localStorage.setItem(WETTBEWERB_STORAGE_KEY, JSON.stringify(w));
									this.appStore.dispatch(WettbewerbActions.aktuellerWettbewerbGeladen({ wettbewerb: w }))
								},
								(error => {
									this.errorHandler.handleError(error);
								})
							)
						}
					}
				),
				first()
			).subscribe();
		}
	}

	public resetState(): void {
		localStorage.removeItem(WETTBEWERB_STORAGE_KEY);
		this.appStore.dispatch(WettbewerbActions.reset());
	}

}
