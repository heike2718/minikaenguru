import { Component, OnInit, OnDestroy } from '@angular/core';
import { SchulteilnahmenFacade } from '../schulteilnahmen.facade';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Subscription, combineLatest } from 'rxjs';
import { UploadComponentModel } from '@minikaenguru-ws/common-components';
import { SchuleUploadModel } from '../schulteilnahmen.model';
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

	uploadModel: UploadComponentModel;

	spracheEnglisch: boolean;

	subUrl = '';



	private schuleUploadModelSubscription: Subscription;

	constructor(private schulteilnahmenFacade: SchulteilnahmenFacade, private router: Router) { }

	ngOnInit(): void {

		this.schuleUploadModelSubscription = this.schuleUploadModel$.subscribe(
			m => {

				if (m) {

					this.subUrl = '/uploads/auswertung/' + m.jahr + '/' + m.katalogData.kuerzelLand + '/' + m.kuerzel;
					this.uploadModel = {subUrl: this.subUrl + '?sprache=de', titel: ''};
				}
			}
		)

	}

	onCheckboxChanged(_event$): void {

		if (this.spracheEnglisch) {
			this.uploadModel = {...this.uploadModel, subUrl: this.subUrl + '?sprache=en'}
		} else {
			this.uploadModel = {...this.uploadModel, subUrl: this.subUrl + '?sprache=de'}
		}

	}

	onDateiAusgewaehlt(event$): void {

		this.schulteilnahmenFacade.dateiAusgewaelt();
	}

	onResponse(rp: ResponsePayload | any): void {

		if (rp) {
			this.schulteilnahmenFacade.auswertungImportiert(rp);
		}
	}

	ngOnDestroy(): void {

		if (this.schuleUploadModelSubscription) {
			this.schuleUploadModelSubscription.unsubscribe();
		}
	}

	gotoSchuleOverview() {
		this.router.navigateByUrl('/schulteilnahme');
	}

	gotoSelectedVeranstalter(): void {
		this.router.navigateByUrl('/veranstalter/details');
	}
}
