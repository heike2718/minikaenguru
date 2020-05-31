import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Wettbewerb } from '../wettbewerbe.model';
import { WettbewerbFacade } from '../../services/wettbewerb.facade';

@Component({
	selector: 'mka-wettbewerb-card',
	templateUrl: './wettbewerb-card.component.html',
	styleUrls: ['./wettbewerb-card.component.css']
})
export class WettbewerbCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	wettbewerb: Wettbewerb;

	wettbewerbUndefined: boolean;

	constructor(private wettbewerbFacade: WettbewerbFacade) { }

	ngOnInit(): void {

		this.wettbewerbUndefined = !this.wettbewerb;
	}


	selectWettbewerb(): void {
		this.wettbewerbFacade.selectWettbewerb(this.wettbewerb);
	}
}
