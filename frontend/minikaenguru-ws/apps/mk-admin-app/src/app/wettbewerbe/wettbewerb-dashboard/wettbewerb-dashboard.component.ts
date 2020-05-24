import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { selectedWettbewerb } from '../+state/wettbewerbe.selectors';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';

@Component({
	selector: 'mka-wettbewerb-dashboard',
	templateUrl: './wettbewerb-dashboard.component.html',
	styleUrls: ['./wettbewerb-dashboard.component.css']
})
export class WettbewerbDashboardComponent implements OnInit {

	devMode = !environment.production;

	wettbewerb$ = this.store.select(selectedWettbewerb);

	constructor(private store: Store<AppState>, private router: Router) { }

	ngOnInit(): void {
	}

	backToWettbewerbe() {

		this.router.navigateByUrl('/wettbewerbe');

	}

	backToDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
