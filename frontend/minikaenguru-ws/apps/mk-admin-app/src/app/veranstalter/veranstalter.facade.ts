import { Injectable } from '@angular/core';
import { VeranstalterService } from './veranstalter.service';
import { Observable, Subscription, of } from 'rxjs';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { LogService } from '@minikaenguru-ws/common-logging';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Veranstalter, VeranstalterSuchanfrage } from './veranstalter.model';
import * as VeranstalterActions from './+state/veranstalter.actions';
import * as VeranstalterSelectors from './+state/veranstalter.selectors';
import { take } from 'rxjs/operators';
import { Router } from '@angular/router';
import { PrivatteilnahmeAdminOverview } from './teilnahmen.model';


@Injectable({
	providedIn: 'root'
})
export class VeranstalterFacade {

	public veranstalters$: Observable<Veranstalter[]> = this.store.select(VeranstalterSelectors.veranstalter);
	public sucheFinished$: Observable<boolean> = this.store.select(VeranstalterSelectors.sucheFinished);
	public loading$: Observable<boolean> = this.store.select(VeranstalterSelectors.veranstalterLoading);
	public selectedVeranstalter$: Observable<Veranstalter> = this.store.select(VeranstalterSelectors.selectedVeranstalter);

	private selectedVeranstalter: Veranstalter;

	constructor(private veranstalterService: VeranstalterService,
		private store: Store<AppState>,
		private router: Router,
		private logger: LogService,
		private errorService: GlobalErrorHandlerService,
	) {

		this.selectedVeranstalter$.subscribe(
			v => this.selectedVeranstalter = v
		);
	}

	public sucheVeranstalter(suchanfrage: VeranstalterSuchanfrage): void {

		this.store.dispatch(VeranstalterActions.startSuche());

		this.veranstalterService.findVeranstalter(suchanfrage).pipe(
			take(1)
		).subscribe(
			(trefferliste: Veranstalter[]) => {
				this.logger.debug('Anzahl Treffer: ' + trefferliste.length);
				this.store.dispatch(VeranstalterActions.sucheFinished({ veranstalter: trefferliste }));
			},
			(error => {
				this.store.dispatch(VeranstalterActions.sucheFinishedWithError());
				this.errorService.handleError(error);
			})
		);
	}

	public trefferlisteLeeren() {
		this.store.dispatch(VeranstalterActions.resetVeranstalters());
	}

	public selectVeranstalter(veranstalter: Veranstalter): void {
		this.logger.debug('veranstalter ' + veranstalter.fullName + ' selected');
		this.store.dispatch(VeranstalterActions.veranstalterSelected({ veranstalter: veranstalter }));
		this.router.navigateByUrl('/veranstalter/details');
	}

	public clearVeranstalterSelection(): void {
		this.store.dispatch(VeranstalterActions.veranstalterSelected({ veranstalter: undefined }));
		this.router.navigateByUrl('/veranstalter');
	}


	public findOrLoadPrivatteilnahmeAdminOverview(teilnahmenummer: string) {


		if (this.selectedVeranstalter !== undefined) {

			if (this.selectedVeranstalter.privatOverview !== undefined) {
				this.store.dispatch(VeranstalterActions.privatteilnahmeOverviewLoaded({ privatteilnahmeOverview: this.selectedVeranstalter.privatOverview }));
				this.router.navigateByUrl('/veranstalter/privatteilnahme');

			} else {

				this.store.dispatch(VeranstalterActions.startLoadDetails());

				this.veranstalterService.loadPrivatteilnahmeAdminOverview(teilnahmenummer).pipe(
					take(1)
				).subscribe(
					(overview: PrivatteilnahmeAdminOverview) => {
						this.store.dispatch(VeranstalterActions.privatteilnahmeOverviewLoaded({ privatteilnahmeOverview: overview }));
						this.router.navigateByUrl('/veranstalter/privatteilnahme');
					},
					(error => {
						this.store.dispatch(VeranstalterActions.loadDetailsFinishedWithError());
						this.errorService.handleError(error);
					})
				)
			}
		}
	}
}
