import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { SchulenFacade } from '../schulen.facade';
import { TeilnahmenFacade } from '../../teilnahmen/teilnahmen.facade';

@Component({
	selector: 'mkv-schule-dashboard',
	templateUrl: './schule-dashboard.component.html',
	styleUrls: ['./schule-dashboard.component.css']
})
export class SchuleDashboardComponent implements OnInit {

	devMode = !environment.production;

	schule$ = this.schulenFacade.selectedSchule$;

	schuleDetails$ = this.schulenFacade.schuleDashboadModel$;

	aktuellerWettbewerb$ = this.teilnahmenFacade.aktuellerWettbewerb$;

	loading$ = this.schulenFacade.loading$;

	constructor(private router: Router, private schulenFacade: SchulenFacade, private teilnahmenFacade: TeilnahmenFacade) {
	}

	ngOnInit(): void {
	}

	backToSchulen(): void {
		this.router.navigate(['/schulen']);
	}

}
