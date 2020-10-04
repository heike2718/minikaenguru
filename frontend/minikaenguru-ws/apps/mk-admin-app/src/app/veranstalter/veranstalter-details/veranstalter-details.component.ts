import { Component, OnInit, OnDestroy } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';
import { Veranstalter } from '../veranstalter.model';
import { Observable, Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
	selector: 'mka-veranstalter-details',
	templateUrl: './veranstalter-details.component.html',
	styleUrls: ['./veranstalter-details.component.css']
})
export class VeranstalterDetailsComponent implements OnInit, OnDestroy {

	selectedVeranstalter$: Observable<Veranstalter> = this.veranstalterFacade.selectedVeranstalter$;

	private veranstalterSubscription: Subscription;

	teilnahmenummernAsString: string;

	constructor(private router: Router, private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {

		this.teilnahmenummernAsString = '';

		this.veranstalterSubscription = this.selectedVeranstalter$.subscribe(
			veranstalter => {
				if (!veranstalter) {
					this.router.navigateByUrl('/veranstalter');
				} else {
					this.teilnahmenummernAsString = this.getTeilnahmenummernAsString(veranstalter);
				}
			}
		);

	}

	private getTeilnahmenummernAsString(veranstalter: Veranstalter): string {

		let result = '';
		veranstalter.teilnahmenummern.forEach(
			t => {
				result+=t;
				result+= ' ';
			}
		);

		return result;
	}

	ngOnDestroy(): void {

		if (this.veranstalterSubscription) {
			this.veranstalterSubscription.unsubscribe();
		}

	}

	gotoTeilnahmen(teilnahmenummer: string): void {
		console.log('jetzt Teilnahmen mit Nummer ' + teilnahmenummer + ' laden');
	}
}
