import { Component, OnInit, OnDestroy } from '@angular/core';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';
import { PrivatteilnahmenFacade } from '../privatteilnahmen.facade';
import { Subscription } from 'rxjs';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mkv-privatteilnahmen-list',
	templateUrl: './privatteilnahmen-list.component.html',
	styleUrls: ['./privatteilnahmen-list.component.css']
})
export class PrivatteilnahmenListComponent implements OnInit, OnDestroy {


	private veranstalterSubscription: Subscription;

	devMode = !environment.production;

	teilnahmen$ = this.teilnahmenFacade.anonymisierteTeilnahmen$;

	teilnahmenummer: string;

	constructor(private veranstalterFacade: PrivatveranstalterFacade, private teilnahmenFacade: PrivatteilnahmenFacade) {

		this.teilnahmenummer = 'unbekannt';
	}

	ngOnInit(): void {

		this.veranstalterSubscription = this.veranstalterFacade.veranstalter$.subscribe(
			veranstalter => {
				if (veranstalter) {
					this.teilnahmenummer = veranstalter.teilnahmenummer;
					this.teilnahmenFacade.ladeAnonymisierteTeilnahmen(this.teilnahmenummer);
				} else {
					this.veranstalterFacade.loadInitialTeilnahmeinfos();
				}
			}
		);

	}

	ngOnDestroy(): void {

		if (this.veranstalterSubscription) {
			this.veranstalterSubscription.unsubscribe();
		}
	}

}
