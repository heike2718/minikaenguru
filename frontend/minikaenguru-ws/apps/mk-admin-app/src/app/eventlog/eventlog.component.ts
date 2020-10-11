import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgbDateStruct, NgbCalendar, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { EventlogFacade } from './eventlog.facade';
import { Subscription } from 'rxjs';
import { DownloadButtonModel } from '@minikaenguru-ws/common-components';

@Component({
	selector: 'mka-eventlog',
	templateUrl: './eventlog.component.html',
	styleUrls: ['./eventlog.component.css']
})
export class EventlogComponent implements OnInit, OnDestroy {



	selectedDatum$ = this.eventlogFacade.selectedDatum$;

	model: NgbDateStruct;
	date: { year: number, month: number };

	downloadButtonModel: DownloadButtonModel;


	minDate: NgbDate = new NgbDate(2020, 8, 26);
	maxDate: NgbDate;

	private selectedDatumSubscription: Subscription;


	constructor(private calendar: NgbCalendar, private eventlogFacade: EventlogFacade) { }

	ngOnInit(): void {

		this.maxDate = this.calendar.getToday();// this.calendar.getNext(this.calendar.getToday());

		this.selectedDatumSubscription = this.selectedDatum$.subscribe(datum => {

			if (datum) {
				this.downloadButtonModel = this.eventlogFacade.createDownloadButtonModel(datum);
			} else {
				this.downloadButtonModel = this.eventlogFacade.createDownloadButtonModelFomNgbDate(this.maxDate);
			}
		});

	}

	ngOnDestroy(): void {

		if (this.selectedDatumSubscription) {
			this.selectedDatumSubscription.unsubscribe();
		}

	}


	onDateSelect($event: NgbDate): void {
		this.eventlogFacade.submitDate($event);

	}

}
