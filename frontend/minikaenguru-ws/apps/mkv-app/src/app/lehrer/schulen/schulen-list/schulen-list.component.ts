import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { KlassenFacade } from '../../../klassen/klassen.facade';

@Component({
	selector: 'mkv-schulen-list',
	templateUrl: './schulen-list.component.html',
	styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent implements OnInit {

	devMode = !environment.production;

	textFeatureFlagAnzeigen = false;
	textFeatureFlag = 'Das ist im Moment noch nicht möglich, kommt aber Anfang 2021.';

	constructor(public lehrerFacade: LehrerFacade
		, private klassenFacade: KlassenFacade
		, private router: Router) {
	}

	ngOnInit(): void {

		this.klassenFacade.resetState();
	}

	addSchule(): void {
		this.lehrerFacade.neueSchulsuche();
		this.router.navigateByUrl('/lehrer/schulen/add-schule')
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/lehrer/dashboard');
	}

	toggleTextFeatureFlagAnzeigen() {
		this.textFeatureFlagAnzeigen = !this.textFeatureFlagAnzeigen;
	}

}
