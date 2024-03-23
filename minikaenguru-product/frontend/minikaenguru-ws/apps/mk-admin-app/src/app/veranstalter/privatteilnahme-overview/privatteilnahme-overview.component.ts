import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { VeranstalterFacade } from '../veranstalter.facade';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mka-privatteilnahme-overview',
	templateUrl: './privatteilnahme-overview.component.html',
	styleUrls: ['./privatteilnahme-overview.component.css']
})
export class PrivatteilnahmeOverviewComponent implements OnInit, OnDestroy {

	veranstalter$ = this.veranstalterFacade.selectedVeranstalter$;

	private veranstalterSubscription: Subscription = new Subscription();

	constructor(private router: Router, private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {

		this.veranstalterSubscription = this.veranstalter$.subscribe(
			veranstalter => {
				if (!veranstalter || !veranstalter.privatOverview) {
					this.router.navigateByUrl('/veranstalter');
				}
			}
		);
	}

	ngOnDestroy(): void {

		this.veranstalterSubscription.unsubscribe();
		this.veranstalterFacade.clearVeranstalterSelection();

	}

}
