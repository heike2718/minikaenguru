import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';

@Component({
	selector: 'mkv-schule-dashboard',
	templateUrl: './schule-dashboard.component.html',
	styleUrls: ['./schule-dashboard.component.css']
})
export class SchuleDashboardComponent implements OnInit {

	devMode = !environment.production;

	schule$ = this.lehrerFacade.selectedSchule$;

	schuleDetails$ = this.lehrerFacade.schuleDetails$;

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	loading$ = this.lehrerFacade.loading$;

	constructor(private router: Router, private lehrerFacade: LehrerFacade, private wettbewerbFacade: WettbewerbFacade) {
	}

	ngOnInit(): void {
	}

	backToSchulen(): void {
		this.lehrerFacade.resetSelection();
		this.router.navigateByUrl('/lehrer/schulen');
	}

}
