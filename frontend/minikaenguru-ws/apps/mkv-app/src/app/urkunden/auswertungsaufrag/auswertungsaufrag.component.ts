import { Component, OnInit, OnDestroy } from '@angular/core';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { UrkundenFacade } from '../urkunden.facade';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Subscription } from 'rxjs';
import { NgbDateStruct, NgbDate, NgbCalendar } from '@ng-bootstrap/ng-bootstrap';
import { UrkundeDateModel, Farbschema, UrkundenauftragSchule } from '../urkunden.model';
import { NgForm } from '@angular/forms';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-auswertungsaufrag',
	templateUrl: './auswertungsaufrag.component.html',
	styleUrls: ['./auswertungsaufrag.component.css']
})
export class AuswertungsaufragComponent implements OnInit, OnDestroy {

	#schule?: Schule;

	dateModel!: NgbDateStruct;

	urkundeDateModel!: UrkundeDateModel;

	farbe!: Farbschema;

	private selectedSchuleSubscription: Subscription = new Subscription();

	constructor(public urkundenFacade: UrkundenFacade,
		public lehrerFacade: LehrerFacade, 
		private calendar: NgbCalendar,
		private logger: LogService) { }

	ngOnInit(): void {

		this.urkundeDateModel = this.urkundenFacade.getUrkundeDateModel();
		this.dateModel = this.calendar.getToday();

		this.selectedSchuleSubscription = this.lehrerFacade.selectedSchule$.subscribe(

			schule => {
				if (schule) {
					this.#schule = schule;
				}
			}
		);

	}

	ngOnDestroy(): void {
		this.selectedSchuleSubscription.unsubscribe();
	}

	onFormSubmit(form: NgForm): void {

		if (!this.#schule) {
			this.logger.debug('selectedSchule was undefined');
			return;
		}

		const value = form.value;

		const auftrag: UrkundenauftragSchule = {
			schulkuerzel: this.#schule.kuerzel,
			dateString: this.urkundeDateModel.selectedDate,
			farbschema: value['farbe']
		};

		this.urkundenFacade.downloadAuswertung(auftrag);
	}

	onCancel(): void {

		this.urkundenFacade.cancelSchulauswertung();
	}


	onDateSelect($event: NgbDate): void {

		const dateString = this.urkundenFacade.calculateDateString($event);
		this.urkundeDateModel = { ...this.urkundeDateModel, selectedDate: dateString };
	}

	zusammenfassung(): string {

		return this.urkundenFacade.zusammenfassungAuswertungsauftrag(this.farbe, this.urkundeDateModel);
	}

}
