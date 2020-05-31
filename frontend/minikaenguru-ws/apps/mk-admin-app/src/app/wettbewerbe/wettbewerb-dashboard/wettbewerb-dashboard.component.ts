import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { WettbewerbFacade } from '../../services/wettbewerb.facade';

@Component({
	selector: 'mka-wettbewerb-dashboard',
	templateUrl: './wettbewerb-dashboard.component.html',
	styleUrls: ['./wettbewerb-dashboard.component.css']
})
export class WettbewerbDashboardComponent implements OnInit {

	devMode = !environment.production;

	wettbewerb$ = this.wettbewerbFacade.wettbewerb$;

	constructor(private wettbewerbFacade: WettbewerbFacade, private router: Router) { }

	ngOnInit(): void {}

	editWettbewerb() {
		this.wettbewerbFacade.editWettbewerb();
	}

	backToWettbewerbe() {
		this.router.navigateByUrl('/wettbewerbe');
	}

	backToDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
