import { Injectable } from '@angular/core';
import * as EventlogActions from './+state/eventlog.actions';
import { selectedDatum } from './+state/eventlog.selectors';
import { environment } from '../../environments/environment';
import { NgbDateStruct, NgbDate } from '@ng-bootstrap/ng-bootstrap';
import { DownloadButtonModel } from '@minikaenguru-ws/common-components';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';


@Injectable({
	providedIn: 'root'
})
export class EventlogFacade {


	public selectedDatum$ = this.store.select(selectedDatum);

	constructor(private store: Store<AppState>) { }


	public submitDate(dateStruct: NgbDateStruct): void {

		const month = this.lpad(dateStruct.month);
		const day = this.lpad(dateStruct.day);

		const date = dateStruct.year + '-' + month + '-' + day;

		this.store.dispatch(EventlogActions.dateSubmitted({ datum: date }));
	}

	public createDownloadButtonModel(dateString: string): DownloadButtonModel {

		const result: DownloadButtonModel = {
			id: 'eventlog-today',
			url: environment.apiUrl + '/events/' + dateString,
			buttonLabel: 'ab ' + dateString,
			dateiname: dateString + '-mkv-gateway-events.log',
			mimetype: 'application/octet-stream',
			tooltip: 'eventlog ab ' + dateString + ' herunterladen',
			class: 'btn btn-outline-dark'
		};

		return result;

	}

	public createDownloadButtonModelFomNgbDate(date: NgbDate): DownloadButtonModel {

		const month = this.lpad(date.month);
		const day = this.lpad(date.day);
		const dateString = date.year + '-' + month + '-' + day;

		return this.createDownloadButtonModel(dateString);

	}


	private lpad(zahl: number): string {

		let result = zahl + '';
		if (result.length === 1) {
			result = '0' + result;
		}

		return result;


	}




}
