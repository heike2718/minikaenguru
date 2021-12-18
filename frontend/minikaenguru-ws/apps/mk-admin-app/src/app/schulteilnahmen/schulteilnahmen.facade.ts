import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { Router } from '@angular/router';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Observable, Subscription, combineLatest } from 'rxjs';

import { SchulteilnahmenService } from './schulteilnahmen.service';
import * as SchulteilnamenActions from './+state/schulteilnahmen.actions';
import * as SchulteilnahmenSelectors from './+state/schulteilnahmen.selectors';
import { SchuleAdminOverview, SchuleAdminOverviewWithID, SchulenOverviewMap, SchuleUploadModel, AuswertungImportReport } from './schulteilnahmen.model';
import { take } from 'rxjs/operators';
import { Teilnahme } from '@minikaenguru-ws/common-components';
import { ResponsePayload, MessageService, Message } from '@minikaenguru-ws/common-messages';
import { UploadsFacade } from '../uploads/uploads.facade';
import { UploadMonitoringInfo } from '../uploads/uploads.model';

@Injectable({
	providedIn: 'root'
})
export class SchulteilnahmenFacade {

	public schuleOverview$: Observable<SchuleAdminOverview | undefined> = this.store.select(SchulteilnahmenSelectors.selectedSchule);

	public schuleUploadModel$: Observable<SchuleUploadModel | undefined> = this.store.select(SchulteilnahmenSelectors.schuleUploadModel);

	public selectedTeilnahme$: Observable<Teilnahme | undefined> = this.store.select(SchulteilnahmenSelectors.selectedTeilnahme);

	public fehlermeldungen$: Observable<string[]> = this.store.select(SchulteilnahmenSelectors.fehlermeldungen);

	public uploadsKlassenlisteInfos$: Observable<UploadMonitoringInfo[]> = this.store.select(SchulteilnahmenSelectors.uploadsKlassenlisteInfos);

	public uploadInfosLOaded$: Observable<boolean> = this.store.select(SchulteilnahmenSelectors.uploadsKlassenlisteInfosLoaded);

	private schulenMap: SchuleAdminOverviewWithID[] = [];

	constructor(private schulteilnahmenService: SchulteilnahmenService,
		private uploadsFacade: UploadsFacade,
		private messageService: MessageService,
		private store: Store<AppState>,
		private router: Router,
		private errorService: GlobalErrorHandlerService) {


		this.store.select(SchulteilnahmenSelectors.schulenMap).subscribe(
			map => this.schulenMap = map
		);

		combineLatest([this.schuleOverview$, this.uploadsFacade.uploadInfos$] ).subscribe(

			result => {

				if (result[0]) {
					const teilnahmenummer = result[0].kuerzel;

					if (result[1]) {
						const uploadInfosKlassenliste: UploadMonitoringInfo[] = result[1].filter(info => teilnahmenummer === info.teilnahmenummer && info.uploadType === 'KLASSENLISTE' );
						this.store.dispatch(SchulteilnamenActions.uploadsKlassenlisteInfosLoaded({uploadInfos: uploadInfosKlassenliste}));
					}
				}
			}
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
					this.store.dispatch(SchulteilnamenActions.loadFinishedWithError());
					this.errorService.handleError(error);
				})
			)
		}
	}



}
