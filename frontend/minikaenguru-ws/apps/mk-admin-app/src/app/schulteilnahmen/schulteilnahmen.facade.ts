import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { Router } from '@angular/router';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Observable } from 'rxjs';

import { SchulteilnahmenService } from './schulteilnahmen.service';
import * as SchulteilnamenActions from './+state/schulteilnahmen.actions';
import * as SchulteilnahmenSelectors from './+state/schulteilnahmen.selectors';
import { SchuleAdminOverview, SchuleAdminOverviewWithID, SchulenOverviewMap, SchuleUploadModel, AuswertungImportReport } from './schulteilnahmen.model';
import { take } from 'rxjs/operators';
import { Teilnahme } from '@minikaenguru-ws/common-components';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';

@Injectable({
	providedIn: 'root'
})
export class SchulteilnahmenFacade {

	public schuleOverview$: Observable<SchuleAdminOverview | undefined> = this.store.select(SchulteilnahmenSelectors.selectedSchule);

	public schuleUploadModel$: Observable<SchuleUploadModel | undefined> = this.store.select(SchulteilnahmenSelectors.schuleUploadModel);

	public selectedTeilnahme$: Observable<Teilnahme | undefined> = this.store.select(SchulteilnahmenSelectors.selectedTeilnahme);

	public fehlermeldungen$: Observable<string[]> = this.store.select(SchulteilnahmenSelectors.fehlermeldungen);

	private schulenMap: SchuleAdminOverviewWithID[] = [];

	constructor(private schulteilnahmenService: SchulteilnahmenService,
		private messageService: MessageService,
		private store: Store<AppState>,
		private router: Router,
		private errorService: GlobalErrorHandlerService) {


		this.store.select(SchulteilnahmenSelectors.schulenMap).subscribe(
			map => this.schulenMap = map
		);

	}

	public clearSchuleSelection(): void {

		this.store.dispatch(SchulteilnamenActions.resetSchulteilnahmen());

	}

	public selectTeilnahme(teilnahme: Teilnahme): void {

		this.store.dispatch(SchulteilnamenActions.anonymisierteTeilnahmeSelected({ teilnahme: teilnahme }));
	}

	public dateiAusgewaelt(): void {

		this.messageService.clear();

		this.store.dispatch(SchulteilnamenActions.dateiAusgewaehlt());

	}

	public auswertungImportiert(responsePayload: ResponsePayload): void {

		if (responsePayload.data) {

			const report: AuswertungImportReport = responsePayload.data;
			this.store.dispatch(SchulteilnamenActions.auswertungImportiert({ report: report }));
		}

		this.messageService.showMessage(responsePayload.message);
	}

	public findOrLoadSchuleAdminOverview(schulkuerzel: string) {

		const map = new SchulenOverviewMap(this.schulenMap);

		if (map.has(schulkuerzel)) {

			this.store.dispatch(SchulteilnamenActions.schuleOverviewLoaded({ schuleAdminOverview: map.get(schulkuerzel) }));
			this.router.navigateByUrl('/schulteilnahmen');
		} else {

			this.store.dispatch(SchulteilnamenActions.startLoadSchule());

			this.schulteilnahmenService.loadSchuleAdminOverview(schulkuerzel).pipe(
				take(1)
			).subscribe(
				(overview: SchuleAdminOverview) => {
					this.store.dispatch(SchulteilnamenActions.schuleOverviewLoaded({ schuleAdminOverview: overview }));
					this.router.navigateByUrl('/schulteilnahme');
				},
				(error => {
					this.store.dispatch(SchulteilnamenActions.loadSchuleFinishedWithError());
					this.errorService.handleError(error);
				})
			)
		}
	}



}
