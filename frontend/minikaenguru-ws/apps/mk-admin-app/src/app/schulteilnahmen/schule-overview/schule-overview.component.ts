import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { SchulteilnahmenFacade } from '../schulteilnahmen.facade';
import { VeranstalterFacade } from '../../veranstalter/veranstalter.facade';

@Component({
	selector: 'mka-schule-overview',
	templateUrl: './schule-overview.component.html',
	styleUrls: ['./schule-overview.component.css']
})
export class SchuleOverviewComponent implements OnInit, OnDestroy {

	schuleOverview$ = this.schulteilnahmenFacade.schuleOverview$;

	statistikUrlPrefix  = environment.apiUrl + '/statistik/';

	private schuleSubscription: Subscription;


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

		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}
		this.schulteilnahmenFacade.clearSchuleSelection();
	}

	gotoSelectedVeranstalter(): void {
		this.router.navigateByUrl('/veranstalter/details');
	}

	gotoVeranstalterList(): void {
		this.veranstalterFacade.clearVeranstalterSelection();
		this.router.navigateByUrl('/veranstalter');
	}
}
