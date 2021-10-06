import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { environment } from '../../environments/environment';

import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { UrkundenService } from './urkunden.service';
import { Kind, DownloadFacade, TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';
import * as UrkundenActions from './+state/urkunden.actions';
import * as UrkundenSelecors from './+state/urkunden.selectors';
import { UrkundenauftragEinzelkind, getLabelFarbe, getLabelUrkundenart, Farbschema, Urkundenart, UrkundeDateModel, UrkundenauftragSchule } from './urkunden.model';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { take } from 'rxjs/operators';
import { NgbDate, NgbDateStruct, NgbCalendar } from '@ng-bootstrap/ng-bootstrap';
import { KinderFacade } from '../kinder/kinder.facade';
import { User, STORAGE_KEY_USER } from '@minikaenguru-ws/common-auth';
import { Schule } from '../lehrer/schulen/schulen.model';
import { LehrerFacade } from '../lehrer/lehrer.facade';


@Injectable({
	providedIn: 'root'
})
export class UrkundenFacade {

	public loading$: Observable<boolean> = this.store.select(UrkundenSelecors.loading);

	public warntext: string;

	private teilnahmeIdentifier?: TeilnahmeIdentifierAktuellerWettbewerb;

	private selectedSchule?: Schule;

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

		const minDate = new NgbDate(heute.year, 1, 1);
		const maxDate = new NgbDate(heute.year, 12, 31);
		const selectedDate = this.calculateDateString(heute);

		return {
			minDate: minDate,
			maxDate: maxDate,
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

	public downloadAuswertung(auftrag: UrkundenauftragSchule): void {
		this.store.dispatch(UrkundenActions.startLoading());

		const defaultFilename = 'schulauswertung_e0f2467a.pdf';

		this.urkundenService.generateSchulauswertung(auftrag).pipe(
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

	zusammenfassungEinzelurkunde(farbe: Farbschema, urkundenart: Urkundenart, nameKind: string, urkundeDateModel: UrkundeDateModel): string {

		const labelFarbe = getLabelFarbe(farbe);
		const labelUrkundenart = getLabelUrkundenart(urkundenart);
		const name = nameKind ? ' für ' + nameKind : '';


		return 'Mit Klick auf den Button "Urkunde erstellen" erstellen Sie eine ' + labelFarbe + ' ' + labelUrkundenart + ' ' + name + ' (Datum ' + urkundeDateModel.selectedDate + ').';
	}

	zusammenfassungAuswertungsauftrag(farbe: Farbschema, urkundeDateModel: UrkundeDateModel): string {

		const labelFarbe = getLabelFarbe(farbe);
		return 'Mit Klick auf den Button "Auswertung erstellen" erstellen Sie die Auswertung: ' + labelFarbe + ' Urkunden mit Datum ' + urkundeDateModel.selectedDate;
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

	public cancelSchulauswertung() {
		this.resetState();
		this.gotoKlassenliste();
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
			+ ' Alle Urkunden für Ihre Schule können Sie zusammen mit einer Auswertungsseite in einem einzigen Schritt in der Ansicht "Klassen" (Klick auf "Klassenliste") erstellen.';
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

	private getUser(): User | undefined{
		const item = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);

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
