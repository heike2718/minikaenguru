import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { WettbewerbFacade } from '../../services/wettbewerb.facade';
import { Subscription } from 'rxjs';
import { Wettbewerb } from '../wettbewerbe.model';

@Component({
	selector: 'mka-wettbewerb-dashboard',
	templateUrl: './wettbewerb-dashboard.component.html',
	styleUrls: ['./wettbewerb-dashboard.component.css']
})
export class WettbewerbDashboardComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	wettbewerb$ = this.wettbewerbFacade.wettbewerb$;

	private wettbewerb: Wettbewerb;

	private selectedWettbewerbSubscription: Subscription;

	constructor(private wettbewerbFacade: WettbewerbFacade, private router: Router) { }

	ngOnInit(): void {

		this.selectedWettbewerbSubscription = this.wettbewerb$.subscribe(
			wb => this.wettbewerb = wb
		);
	}

	ngOnDestroy(): void {

		if (this.selectedWettbewerbSubscription) {
			this.selectedWettbewerbSubscription.unsubscribe();
		}

	}

	editWettbewerb() {
		this.wettbewerbFacade.editWettbewerb(this.wettbewerb);
	}

	backToWettbewerbe() {
		this.router.navigateByUrl('/wettbewerbe');
	}

	backToDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
