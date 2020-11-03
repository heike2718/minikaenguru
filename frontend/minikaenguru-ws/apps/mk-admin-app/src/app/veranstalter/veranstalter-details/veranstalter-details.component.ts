import { Component, OnInit, OnDestroy } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';
import { Veranstalter } from '../veranstalter.model';
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { SchulteilnahmenFacade } from '../../schulteilnahmen/schulteilnahmen.facade';
import { Rolle } from '@minikaenguru-ws/common-auth';

@Component({
	selector: 'mka-veranstalter-details',
	templateUrl: './veranstalter-details.component.html',
	styleUrls: ['./veranstalter-details.component.css']
})
export class VeranstalterDetailsComponent implements OnInit, OnDestroy {

	selectedVeranstalter$: Observable<Veranstalter> = this.veranstalterFacade.selectedVeranstalter$;

	private veranstalterSubscription: Subscription;

	teilnahmenummernAsString: string;

	private rolle: Rolle;

	constructor(private router: Router, private veranstalterFacade: VeranstalterFacade, private schulteilnahmenFacade: SchulteilnahmenFacade) { }

	ngOnInit(): void {

		this.teilnahmenummernAsString = '';

		this.veranstalterSubscription = this.selectedVeranstalter$.subscribe(
			veranstalter => {
				if (!veranstalter) {
					this.router.navigateByUrl('/veranstalter');
				} else {
					this.teilnahmenummernAsString = this.getTeilnahmenummernAsString(veranstalter);
					this.rolle = veranstalter.rolle;
				}
			}
		);

	}

	ngOnDestroy(): void {

		if (this.veranstalterSubscription) {
			this.veranstalterSubscription.unsubscribe();
		}

	}

	private getTeilnahmenummernAsString(veranstalter: Veranstalter): string {

		let result = '';
		veranstalter.teilnahmenummern.forEach(
			t => {
				result += t;
				result += ' ';
			}
		);

		return result;
	}

	gotoTeilnahmen(teilnahmenummer: string): void {

		if (this.rolle && this.rolle === 'LEHRER') {
			this.schulteilnahmenFacade.findOrLoadSchuleAdminOverview(teilnahmenummer);
		} else {
			this.veranstalterFacade.findOrLoadPrivatteilnahmeAdminOverview(teilnahmenummer);
		}
	}
}


