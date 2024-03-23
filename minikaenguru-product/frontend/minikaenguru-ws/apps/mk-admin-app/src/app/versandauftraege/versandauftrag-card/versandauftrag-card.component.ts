import { Component, OnInit, Input } from '@angular/core';
import { Versandauftrag } from '../../shared/newsletter-versandauftrage.model';
import { environment } from '../../../environments/environment';
import { VersandauftraegeFacade } from '../versandauftraege.facade';

@Component({
	selector: 'mka-versandauftrag-card',
	templateUrl: './versandauftrag-card.component.html',
	styleUrls: ['./versandauftrag-card.component.css']
})
export class VersandauftragCardComponent implements OnInit {

	devMode = environment.envName === 'DEV'

	@Input()
	versandauftrag!: Versandauftrag;

	constructor(public versandauftraegeFacade: VersandauftraegeFacade) { }

	ngOnInit(): void { }
}
