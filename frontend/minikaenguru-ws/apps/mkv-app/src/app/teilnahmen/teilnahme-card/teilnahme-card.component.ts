import { Component, OnInit, Input } from '@angular/core';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mkv-teilnahme-card',
	templateUrl: './teilnahme-card.component.html',
	styleUrls: ['./teilnahme-card.component.css']
})
export class TeilnahmeCardComponent implements OnInit {

	devMode = !environment.production;
	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';


	@Input() teilnahme: AnonymisierteTeilnahme;
	constructor() { }

	ngOnInit(): void {
	}

	getStatistic(): void {
		console.log('jetzt Statistik für die Teilnahme mit identifier erstellen: ' + JSON.stringify(this.teilnahme.identifier));
		this.textFeatureFlagAnzeigen = true;
	}

	toggleTextFeatureFlagAnzeigen(): void {
		this.textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber im Herbst 2020.';
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}
}
