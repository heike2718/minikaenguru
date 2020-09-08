import { Component, Input, OnInit } from '@angular/core';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mkv-privatteilnahme-card',
	templateUrl: './privatteilnahme-card.component.html',
	styleUrls: ['./privatteilnahme-card.component.css']
})
export class PrivatteilnahmeCardComponent implements OnInit {

	devMode = !environment.production;

	@Input() teilnahme: AnonymisierteTeilnahme;

	constructor() { }

	ngOnInit(): void {
	}


	getStatistic(): void {
		console.log('jetzt Statistik für die Teilnahme mit identifier erstellen: ' + JSON.stringify(this.teilnahme.identifier));
	}

}
