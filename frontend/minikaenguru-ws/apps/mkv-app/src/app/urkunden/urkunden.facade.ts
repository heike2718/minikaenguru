import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { environment } from '../../environments/environment';

import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { UrkundenService } from './urkunden.service';
import { Kind, DownloadFacade, TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';
import * as UrkundenActions from './+state/urkunden.actions';
import * as UrkundenSelecors from './+state/urkunden.selectors';
import { UrkundenauftragEinzelkind, getLabelFarbe, getLabelUrkundenart, Farbschema, Urkundenart, UrkundeDateModel } from './urkunden.model';
import { Router } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import { NgbDate, NgbDateStruct, NgbCalendar } from '@ng-bootstrap/ng-bootstrap';
import { KinderFacade } from '../kinder/kinder.facade';
import { User } from '@minikaenguru-ws/common-auth';
import { Wettbewerb } from '../wettbewerb/wettbewerb.model';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';
import { Schule } from '../lehrer/schulen/schulen.model';
import { LehrerFacade } from '../lehrer/lehrer.facade';


@Injectable({
	providedIn: 'root'
})
export class UrkundenFacade {

	public loading$: Observable<boolean> = this.store.select(UrkundenSelecors.loading);

	public warntext: string;

	private teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb;

	private selectedSchule: Schule;

	constructor(private store: Store<AppState>,
		private urkundenService: UrkundenService,
		private kinderFacade: KinderFacade,
		private lehrerFacace: LehrerFacade,
		private downloadFacade: DownloadFacade,
		private calendar: NgbCalendar,
		private router: Router,
		private errorHandler: GlobalErrorHandlerService,) {

		this.kinderFacade.teilnahmeIdentifier$.subscribe(

			ti => this.teilnahmeIdentifier = ti
		);

		this.lehrerFacace.selectedSchule$.subscribe(
			schule => this.selectedSchule = schule
		);

		this.warntext = this.warntextFuerLehrer();

	}

	public getUrkundeDateModel(): UrkundeDateModel {

		const heute = this.calendar.getToday();
		const minDate = new NgbDate(heute.year, 3, 1);
		const selectedDate = this.calculateDateString(heute);

		return {
			minDate: minDate,
			maxDate: heute,
			selectedDate: selectedDate
		};
	}


	public downloadUrkunde(auftrag: UrkundenauftragEinzelkind): void {

		this.store.dispatch(UrkundenActions.startLoading());

		const defaultFilename = 'teilnahmeurkunde_e0f2467a.pdf';


		this.urkundenService.generateUrkunde(auftrag).pipe(
			take(1)
		).subscribe(
			blob => {
				this.downloadFacade.saveAs(blob, defaultFilename);
				this.store.dispatch(UrkundenActions.downloadFinished());
			},
			(error => {
				this.store.dispatch(UrkundenActions.downloadFinished());
				this.errorHandler.handleError(error);
			})
		);
	}

	zusammenfassung(farbe: Farbschema, urkundenart: Urkundenart, nameKind: string, urkundeDateModel: UrkundeDateModel): string {

		const labelFarbe = getLabelFarbe(farbe);
		const labelUrkundenart = getLabelUrkundenart(urkundenart);
		const name = nameKind ? ' für ' + nameKind : '';


		return 'Mit Klick auf den Button "Urkunde erstellen" erstellen Sie eine ' + labelFarbe + ' ' + labelUrkundenart + ' ' + name + ' (Datum ' + urkundeDateModel.selectedDate + ').';
	}


	public calculateDateString(dateStruct: NgbDateStruct): string {

		if (!dateStruct) {
			return '';
		}

		const month = this.lpad(dateStruct.month);
		const day = this.lpad(dateStruct.day);

		return day + '.' + month + '.' + dateStruct.year;
	}

	public cancelEinzelurkunde() {
		this.resetState();
		this.navigateBack();
	}

	public gotoKlassenliste(): void {

		if (this.selectedSchule) {
			this.resetState();
			const url = 'klassen/' + this.selectedSchule.kuerzel;
			this.router.navigateByUrl(url);
		}
	}

	public resetState(): void {

		this.store.dispatch(UrkundenActions.resetModule());

	}


	// ///////////////////////////////////////////////////////////////////////////////////////////////////

	private warntextFuerLehrer(): string {

		const user = this.getUser();
		if (!user || user.rolle !== 'LEHRER') {
			return '';
		}

		return 'Bitte verwenden Sie diese Funktion nur, um Urkunden für einzelne Kinder zu korrigieren.'
			+ ' Die Urkunden für Ihre Schule erstellen Sie bitte zusammen mit einer Auswertungsseite in der Ansicht "Klassen" (Klick auf "Klassenliste") für die ganze Schule.';
	}


	private navigateBack(): void {

		if (this.teilnahmeIdentifier && this.teilnahmeIdentifier.teilnahmenummer) {
			this.router.navigateByUrl('/kinder/' + this.teilnahmeIdentifier.teilnahmenummer);
		} else {

			const user = this.getUser();

			if (user) {
				switch (user.rolle) {
					case 'LEHRER':
						this.router.navigateByUrl('/lehrer/dashboard');
						break;
					case 'PRIVAT':
						this.router.navigateByUrl('/privat/dashboard');
						break;
					default: this.router.navigateByUrl('/landing');
				}
			}
		}
	}

	private getUser(): User {
		const item = localStorage.getItem(environment.storageKeyPrefix + 'user');

		if (item) {
			return JSON.parse(item);
		}

		return undefined;
	}


	private lpad(zahl: number): string {

		let result = zahl + '';
		if (result.length === 1) {
			result = '0' + result;
		}

		return result;
	}


}