import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../environments/environment';
import { PrivatauswertungFacade } from '../privatauswertung.facade';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkv-kinder-list',
	templateUrl: './kinder-list.component.html',
	styleUrls: ['./kinder-list.component.css']
})
export class KinderListComponent implements OnInit, OnDestroy {


	devMode = !environment.production;

	kinder$ = this.privatauswertungFacade.kinder$;
	anzahlKinder$ = this.privatauswertungFacade.anzahlKinder$;

	veranstalter$ = this.veranstalterFacade.veranstalter$;

	private veranstalterSubscription: Subscription;

	constructor(private privatauswertungFacade: PrivatauswertungFacade,
		private veranstalterFacade: PrivatveranstalterFacade) { }

	ngOnInit(): void {

		this.veranstalterSubscription = this.veranstalter$.subscribe(
			v => {
				if (v === undefined) {
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
