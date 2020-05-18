import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { teilnahmenummern } from './+state/veranstalter.selectors';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {


	teilnahmenummern$ = this.appStore.select(teilnahmenummern);

	constructor(private appStore: Store<AppState>, private router: Router) { }

	gotoSchulen() {
		this.router.navigateByUrl('/schulen');
	}
}
