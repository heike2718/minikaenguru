import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { SchulteilnahmenFacade } from '../schulteilnahmen.facade';
import { VeranstalterFacade } from '../../veranstalter/veranstalter.facade';
import { Teilnahme } from '@minikaenguru-ws/common-components';
import { UploadsFacade } from '../../uploads/uploads.facade';
import { UploadType } from '../../uploads/uploads.model';

@Component({
	selector: 'mka-schule-overview',
	templateUrl: './schule-overview.component.html',
	styleUrls: ['./schule-overview.component.css']
})
export class SchuleOverviewComponent implements OnInit, OnDestroy {

	schuleOverview$ = this.schulteilnahmenFacade.schuleOverview$;
	uploadsKlassenlisteInfos$ = this.schulteilnahmenFacade.uploadsKlassenlisteInfos$;

	statistikUrlPrefix = environment.apiUrl + '/statistik/';

	message: string = '';

	private schuleSubscription: Subscription = new Subscription();

	private uploadInfosSubscription: Subscription = new Subscription();

	private preserveSelectedSchule = false;

	private teilnahmenummer: string | undefined;

	constructor(private router: Router,
		private schulteilnahmenFacade: SchulteilnahmenFacade,
		private veranstalterFacade: VeranstalterFacade,
		private uploadsFacade: UploadsFacade) { }

	ngOnInit(): void {

		this.schuleSubscription = this.schuleOverview$.subscribe(
			schule => {
				if (!schule) {
					this.router.navigateByUrl('/veranstalter');
				} else {
					this.teilnahmenummer = schule.kuerzel;
				}
			}
		);
	}

	ngOnDestroy(): void {
		this.schuleSubscription.unsubscribe();
		if (!this.preserveSelectedSchule) {
			this.schulteilnahmenFacade.clearSchuleSelection();
		}
		this.uploadInfosSubscription.unsubscribe();
	}

	onUploadButtonClicked(event: Teilnahme | any): void {
		this.preserveSelectedSchule = true;
		this.schulteilnahmenFacade.selectTeilnahme(event);
		this.router.navigateByUrl('/upload-auswertung');
	}

	loadUploadInfosKlassenimport(): void {

		if (this.teilnahmenummer) {

			const uploadType: UploadType = 'KLASSENLISTE';
			this.uploadsFacade.getOrLoadUploadInfos(uploadType, this.teilnahmenummer);			
		}
	}

	gotoSelectedVeranstalter(): void {
		this.router.navigateByUrl('/veranstalter/details');
	}

	gotoVeranstalterList(): void {
		this.veranstalterFacade.clearVeranstalterSelection();
		this.router.navigateByUrl('/veranstalter');
	}

	gotoUploadAuswertung(): void {
		this.preserveSelectedSchule = true;
		this.router.navigateByUrl('upload-auswertung');
	}
}
