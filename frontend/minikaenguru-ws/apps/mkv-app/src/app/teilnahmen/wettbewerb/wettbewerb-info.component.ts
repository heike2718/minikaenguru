import { Component, OnInit } from '@angular/core';
import { TeilnahmenFacade } from '../teilnahmen.facade';

@Component({
	selector: 'mkv-wettbewerb-info',
	templateUrl: './wettbewerb-info.component.html',
	styleUrls: ['./wettbewerb-info.component.css']
})
export class WettbewerbInfoComponent implements OnInit {


	aktuellerWettbewerb$ = this.teilnahmenFacade.aktuellerWettbewerb$;

	constructor(private teilnahmenFacade: TeilnahmenFacade) { }

	ngOnInit(): void {
	}

}
