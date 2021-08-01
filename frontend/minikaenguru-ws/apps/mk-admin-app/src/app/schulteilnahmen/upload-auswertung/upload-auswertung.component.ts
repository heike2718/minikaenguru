import { Component, OnInit, OnDestroy } from '@angular/core';
import { SchulteilnahmenFacade } from '../schulteilnahmen.facade';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs';
import { UploadComponentModel } from '@minikaenguru-ws/common-components';


@Component({
	selector: 'mka-upload-auswertung',
	templateUrl: './upload-auswertung.component.html',
	styleUrls: ['./upload-auswertung.component.css']
})
export class UploadAuswertungComponent implements OnInit, OnDestroy {

	schuleOverview$ = this.schulteilnahmenFacade.schuleOverview$;

	uploadModel: UploadComponentModel;

	private selectedSchuleSubscription: Subscription;

	private preserveSchuleSelection = false;



	constructor(private schulteilnahmenFacade: SchulteilnahmenFacade, private router: Router) { }

	ngOnInit(): void {

		this.selectedSchuleSubscription = this.schuleOverview$.subscribe(
			s => {
				if (s) {

					this.uploadModel = { subUrl: '/uploads/auswertung/2020/' + s.katalogData.kuerzelLand + '/' + s.kuerzel + '?sprache=de', titel: '' }
				} else {
					this.uploadModel = undefined;
				}
			}
		)
	}

	ngOnDestroy(): void {

		if (this.selectedSchuleSubscription) {
			this.selectedSchuleSubscription.unsubscribe();
		}

		if (!this.preserveSchuleSelection) {
			this.schulteilnahmenFacade.clearSchuleSelection();
		}
	}

	gotoSchuleOverview() {

		this.preserveSchuleSelection = true;
		this.router.navigateByUrl('/schulteilnahme');
	}

	gotoSelectedVeranstalter(): void {
		this.router.navigateByUrl('/veranstalter/details');
	}
}
