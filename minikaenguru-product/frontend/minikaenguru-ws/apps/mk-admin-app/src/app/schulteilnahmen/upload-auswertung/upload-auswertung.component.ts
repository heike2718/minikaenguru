import { Component, OnInit, OnDestroy } from '@angular/core';
import { SchulteilnahmenFacade } from '../schulteilnahmen.facade';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { initialUploadComponentModel, UploadComponentModel } from '@minikaenguru-ws/common-components';
import { ResponsePayload } from '@minikaenguru-ws/common-messages';


@Component({
	selector: 'mka-upload-auswertung',
	templateUrl: './upload-auswertung.component.html',
	styleUrls: ['./upload-auswertung.component.css']
})
export class UploadAuswertungComponent implements OnInit, OnDestroy {

	schuleUploadModel$ = this.schulteilnahmenFacade.schuleUploadModel$;

	selectedTeilnahme$ = this.schulteilnahmenFacade.selectedTeilnahme$;

	fehlermeldungen$ = this.schulteilnahmenFacade.fehlermeldungen$;

	uploadModel: UploadComponentModel = initialUploadComponentModel;

	spracheEnglisch: boolean = false;

	subUrl = '';

	private schuleUploadModelSubscription: Subscription = new Subscription();

	constructor(private schulteilnahmenFacade: SchulteilnahmenFacade, private router: Router) { }

	ngOnInit(): void {

		this.schuleUploadModelSubscription = this.schuleUploadModel$.subscribe(
			m => {

				if (m) {

					this.subUrl = '/uploads/auswertung/' + m.jahr + '/' + m.katalogData.kuerzelLand + '/' + m.kuerzel;
					this.uploadModel = {
						subUrl: this.subUrl + '?sprache=de'
						, titel: ''
						// , accept: '.*'
						, accept: '.csv, .ods, .xlsx'
						, maxSizeBytes: 2097152
						// , maxSizeBytes: 61440
						, errorMessageSize: 'Die Datei ist zu groß. Die maximale erlaubte Größe ist 2 MB'
						, acceptMessage: 'Erlaubte Dateitypen: xlsx, ods, csv. Achtung! Das alte MS-Office-Format xls wird nicht unterstützt.'
					};
				}
			}
		);
	}

	ngOnDestroy(): void {

    	this.schuleUploadModelSubscription.unsubscribe();
	}

	onCheckboxChanged(): void {
		
		if (this.spracheEnglisch) {
			this.uploadModel = { ...this.uploadModel, subUrl: this.subUrl + '?sprache=en' }
		} else {
			this.uploadModel = { ...this.uploadModel, subUrl: this.subUrl + '?sprache=de' }
		}

	}

	onDateiAusgewaehlt(_event$ : any): void {

		this.schulteilnahmenFacade.dateiAusgewaelt();
	}

	onResponse(rp: ResponsePayload | any): void {

		if (rp) {
			this.schulteilnahmenFacade.auswertungImportiert(rp);
		}
	}

	gotoSchuleOverview() {
		this.router.navigateByUrl('/schulteilnahme');
	}

	gotoSelectedVeranstalter(): void {
		this.router.navigateByUrl('/veranstalter/details');
	}
}
