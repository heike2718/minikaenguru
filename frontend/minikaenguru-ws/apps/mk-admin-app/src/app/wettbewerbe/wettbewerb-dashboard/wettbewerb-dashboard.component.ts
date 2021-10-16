import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { WettbewerbFacade } from '../../services/wettbewerb.facade';
import { Subscription } from 'rxjs';
import { Wettbewerb } from '../wettbewerbe.model';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mka-wettbewerb-dashboard',
	templateUrl: './wettbewerb-dashboard.component.html',
	styleUrls: ['./wettbewerb-dashboard.component.css']
})
export class WettbewerbDashboardComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	wettbewerb$ = this.wettbewerbFacade.wettbewerb$;

	private wettbewerb?: Wettbewerb;

	private selectedWettbewerbSubscription: Subscription = new Subscription();

	constructor(private wettbewerbFacade: WettbewerbFacade, private router: Router, private logger: LogService) { }

	ngOnInit(): void {

		this.selectedWettbewerbSubscription = this.wettbewerb$.subscribe(
			wb => this.wettbewerb = wb
		);
	}

	ngOnDestroy(): void {
		this.selectedWettbewerbSubscription.unsubscribe();
	}

	editWettbewerb() {
		if (this.wettbewerb) {
			this.wettbewerbFacade.editWettbewerb(this.wettbewerb);
		} else {
			this.logger.debug('wettwerb was undefined');
		}		
	}

	moveToNextStatus() {
		if (this.wettbewerb) {
			this.wettbewerbFacade.moveWettbewerbOn(this.wettbewerb);
		} else {
			this.logger.debug('wettwerb was undefined');
		}	
		
	}

	backToWettbewerbe() {
		this.router.navigateByUrl('/wettbewerbe');
	}

	backToDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
