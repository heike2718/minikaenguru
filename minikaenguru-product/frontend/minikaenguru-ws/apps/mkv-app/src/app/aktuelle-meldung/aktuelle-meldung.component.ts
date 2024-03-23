import { Component, OnInit, OnDestroy } from '@angular/core';
import { AktuelleMeldungFacade } from './aktuelle-meldung.facade';
import { Subscription } from 'rxjs';
import { AktuelleMeldung } from './aktuelle-meldung.model';

@Component({
	selector: 'mkv-aktuelle-meldung',
	templateUrl: './aktuelle-meldung.component.html',
	styleUrls: ['./aktuelle-meldung.component.css']
})
export class AktuelleMeldungComponent implements OnInit, OnDestroy {

	private aktuelleMeldungSubscription: Subscription = new Subscription();

	aktuelleMeldungGeladen$ = this.aktuelleMeldungFacade.aktuelleMeldungGeladen$;
	aktuelleMeldung?: AktuelleMeldung;

	constructor(private aktuelleMeldungFacade: AktuelleMeldungFacade) { }

	ngOnInit(): void {

		this.aktuelleMeldungSubscription = this.aktuelleMeldungFacade.aktuelleMeldung$.subscribe(
			m => this.aktuelleMeldung = m
		);
	}

	ngOnDestroy(): void {
		this.aktuelleMeldungSubscription.unsubscribe();
	}

}
