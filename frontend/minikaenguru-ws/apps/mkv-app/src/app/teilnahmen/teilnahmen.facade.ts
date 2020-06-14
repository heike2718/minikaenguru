import { Injectable } from '@angular/core';
import { TeilnahmenService } from '../services/teilnahmen.service';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import * as TeilnahmenActions from './+state/teilnahmen.actions';
import { aktuellerWettbewerb, hatZugangZuUnterlagen } from './+state/teilnahmen.selectors';
import { tap, first } from 'rxjs/operators';
import { Wettbewerb } from './teilnahmen.model';

const WETTBEWERB_STORAGE_KEY = 'mkv_wettbewerb';

@Injectable({ providedIn: 'root' })
export class TeilnahmenFacade {

	public aktuellerWettbewerb$ = this.appStore.select(aktuellerWettbewerb);
	public hatZugangZuUnterlagen$ = this.appStore.select(hatZugangZuUnterlagen);

	constructor(private appStore: Store<AppState>,
		private teilnahmenService: TeilnahmenService,
		private errorHandler: GlobalErrorHandlerService) { }


	public ladeAktuellenWettbewerb(): void {

		const w = localStorage.getItem(WETTBEWERB_STORAGE_KEY);
		if (w) {
			const wettbewerb: Wettbewerb = JSON.parse(w);
			this.appStore.dispatch(TeilnahmenActions.aktuellerWettbewerbGeladen({ wettbewerb: wettbewerb }))

		} else {

			this.aktuellerWettbewerb$.pipe(
				tap(
					wettbewerb => {
						if (!wettbewerb) {
							this.teilnahmenService.getAktuellenWettbewerb().subscribe(
								w => {
									localStorage.setItem(WETTBEWERB_STORAGE_KEY, JSON.stringify(w));
									this.appStore.dispatch(TeilnahmenActions.aktuellerWettbewerbGeladen({ wettbewerb: w }))
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

	public ladeStatusZugangUnterlagen() {

	}

	public resetState(): void {

		localStorage.removeItem(WETTBEWERB_STORAGE_KEY);


	}
}
