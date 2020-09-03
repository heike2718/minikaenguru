import { Component, OnInit } from '@angular/core';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';
import { AktuelleMeldungFacade } from '../aktuelle-meldung/aktuelle-meldung.facade';

@Component({
	selector: 'mkv-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {

	aktuelleMeldungNichtLeer$ = this.aktuelleMeldungFacade.aktuelleMeldungNichtLeer$;

	constructor(private wettbewerbFacade: WettbewerbFacade, private aktuelleMeldungFacade: AktuelleMeldungFacade) { }

	ngOnInit(): void {
		this.wettbewerbFacade.ladeAktuellenWettbewerb();
		this.aktuelleMeldungFacade.ladeAktuelleMeldung();
	}
}
