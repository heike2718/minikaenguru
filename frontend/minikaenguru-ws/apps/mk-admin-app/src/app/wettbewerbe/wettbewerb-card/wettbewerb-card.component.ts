import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Wettbewerb } from '../wettbewerbe.model';
import { AdminWettbewerbFacade } from '../../services/admin-wettbewerb.facade';

@Component({
	selector: 'mka-wettbewerb-card',
	templateUrl: './wettbewerb-card.component.html',
	styleUrls: ['./wettbewerb-card.component.css']
})
export class WettbewerbCardComponent implements OnInit {

	devMode = environment.envName === 'DEV';

	@Input()
	wettbewerb!: Wettbewerb;

	wettbewerbUndefined: boolean = false;

	constructor(private wettbewerbFacade: AdminWettbewerbFacade) { }

	ngOnInit(): void {

		this.wettbewerbUndefined = !this.wettbewerb;
	}


	selectWettbewerb(): void {
		this.wettbewerbFacade.selectWettbewerb(this.wettbewerb);
	}
}
