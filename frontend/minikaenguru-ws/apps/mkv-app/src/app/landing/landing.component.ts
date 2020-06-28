import { Component, OnInit } from '@angular/core';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';

@Component({
	selector: 'mkv-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {


	constructor(private wettbewerbFacade: WettbewerbFacade) { }

	ngOnInit(): void {
		this.wettbewerbFacade.ladeAktuellenWettbewerb();
	}


}
