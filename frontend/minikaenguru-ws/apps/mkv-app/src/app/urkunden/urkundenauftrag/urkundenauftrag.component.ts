import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgbCalendar, NgbDateStruct, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { UrkundenFacade } from '../urkunden.facade';
import { Subscription } from 'rxjs';
import { Kind, kindToString, TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';
import { KinderFacade } from '../../kinder/kinder.facade';
import { Urkundenart, Farbschema, UrkundenauftragEinzelkind, getLabelFarbe, getLabelUrkundenart } from '../urkunden.model';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { User } from '@minikaenguru-ws/common-auth';

@Component({
	selector: 'mkv-urkundenauftrag',
	templateUrl: './urkundenauftrag.component.html',
	styleUrls: ['./urkundenauftrag.component.css']
})
export class UrkundenauftragComponent implements OnInit, OnDestroy {


	kind: Kind;

	nameKind: string;
	dateModel: NgbDateStruct;

	minDate: NgbDate = new NgbDate(2020, 12, 26);
	maxDate: NgbDate;

	urkundenart: Urkundenart;
	farbe: Farbschema;

	dateString = '';

	private teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb;

	private selectedKindSubscription: Subscription;

	private teilnahmeIdentifierSubscription: Subscription;

	constructor(private calendar: NgbCalendar,
		public urkundenFacade: UrkundenFacade,
		private kinderFacade: KinderFacade,
		private router: Router) { }

	ngOnInit(): void {

		this.maxDate = this.calendar.getToday();
		this.dateModel = this.maxDate;
		this.dateString = this.calcDateString(this.maxDate);

		this.urkundenart = 'TEILNAHME';
		this.farbe = 'GREEN';

		this.selectedKindSubscription = this.kinderFacade.selectedKind$.subscribe(

			kind => {
				if (kind) {
					this.kind = kind;
					this.nameKind = kindToString(kind);
				}
			}
		);

		this.teilnahmeIdentifierSubscription = this.kinderFacade.teilnahmeIdentifier$.subscribe(
			ti => this.teilnahmeIdentifier = ti
		);
	}

	ngOnDestroy(): void {

		if (this.selectedKindSubscription) {
			this.selectedKindSubscription.unsubscribe();
		}

		if (this.teilnahmeIdentifierSubscription) {
			this.teilnahmeIdentifierSubscription.unsubscribe();
		}

	}


	onDateSelect($event: NgbDate): void {

		this.dateString = this.calcDateString($event);
	}

	onFormSubmit(form: NgForm): void {

		const value = form.value;

		const auftrag: UrkundenauftragEinzelkind = {
			kindUuid: this.kind.uuid,
			dateString: this.dateString,
			farbschema: value['farbe'],
			urkundenart: value['urkundenart']
		};

		this.urkundenFacade.downloadUrkunde(auftrag);
	}


	onCancel(): void {

		this.urkundenFacade.resetState();
		this.navigateBack();

	}

	zusammenfassung(): string {

		const labelFarbe = getLabelFarbe(this.farbe);
		const labelUrkundenart = getLabelUrkundenart(this.urkundenart);
		const name =  this.nameKind ? ' für ' + this.nameKind : '';

		return 'Mit Klick auf den Button "Urkunde erstellen" erstellen Sie eine ' + labelFarbe + ' ' + labelUrkundenart + ' '+ name + ' (Datum ' + this.dateString + ').';
	}


	private navigateBack(): void {

		if (this.teilnahmeIdentifier && this.teilnahmeIdentifier.teilnahmenummer) {
			this.router.navigateByUrl('/kinder/' + this.teilnahmeIdentifier.teilnahmenummer);
		} else {

			const item = localStorage.getItem(environment.storageKeyPrefix + 'user');
			if (item) {
				const user: User = JSON.parse(item);

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

	private calcDateString(dateStruct: NgbDate): string {
		const month = this.lpad(dateStruct.month);
		const day = this.lpad(dateStruct.day);

		return day + '.' + month + '.' + dateStruct.year;
	}


	private lpad(zahl: number): string {

		let result = zahl + '';
		if (result.length === 1) {
			result = '0' + result;
		}

		return result;
	}

}
