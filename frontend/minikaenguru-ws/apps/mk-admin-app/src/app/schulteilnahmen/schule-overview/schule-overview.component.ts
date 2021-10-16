import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { SchulteilnahmenFacade } from '../schulteilnahmen.facade';
import { VeranstalterFacade } from '../../veranstalter/veranstalter.facade';
import { Teilnahme } from '@minikaenguru-ws/common-components';

@Component({
	selector: 'mka-schule-overview',
	templateUrl: './schule-overview.component.html',
	styleUrls: ['./schule-overview.component.css']
})
export class SchuleOverviewComponent implements OnInit, OnDestroy {

	schuleOverview$ = this.schulteilnahmenFacade.schuleOverview$;

	statistikUrlPrefix = environment.apiUrl + '/statistik/';

	message: string = '';

	private schuleSubscription: Subscription = new Subscription();

	private preserveSelectedSchule = false;


	constructor(private router: Router, private schulteilnahmenFacade: SchulteilnahmenFacade, private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {

		this.schuleSubscription = this.schuleOverview$.subscribe(
			schule => {
				if (!schule) {
					this.router.navigateByUrl('/veranstalter');
				}
			}
		)
	}

	ngOnDestroy(): void {
		this.schuleSubscription.unsubscribe();
		if (!this.preserveSelectedSchule) {
			this.schulteilnahmenFacade.clearSchuleSelection();
		}
	}

	onUploadButtonClicked(event: Teilnahme | any): void {
		this.preserveSelectedSchule = true;
		this.schulteilnahmenFacade.selectTeilnahme(event);
		this.router.navigateByUrl('/upload-auswertung');
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
