import { Component, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { selectedWettbewerb } from '../+state/wettbewerbe.selectors';
import { Router, ActivatedRoute } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mka-wettbewerb-dashboard',
	templateUrl: './wettbewerb-dashboard.component.html',
	styleUrls: ['./wettbewerb-dashboard.component.css']
})
export class WettbewerbDashboardComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	wettbewerb$ = this.store.select(selectedWettbewerb);

	private id: string;

	private wettbewerbSubscription: Subscription;

	constructor(private store: Store<AppState>, private router: Router) { }

	ngOnInit(): void {
		this.wettbewerbSubscription = this.wettbewerb$.subscribe(
			wb => this.id = '' + wb.jahr
		);
	}

	ngOnDestroy(): void {
		if (this.wettbewerbSubscription) {
			this.wettbewerbSubscription.unsubscribe();
		}
	}

	editWettbewerb() {
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/' + this.id);
	}

	backToWettbewerbe() {

		this.router.navigateByUrl('/wettbewerbe');

	}

	backToDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
