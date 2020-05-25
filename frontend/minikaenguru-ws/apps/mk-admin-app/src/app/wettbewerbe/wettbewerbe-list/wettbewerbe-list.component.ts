import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { wettbewerbe } from '../+state/wettbewerbe.selectors';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { createNewWettbewerb } from '../+state/wettbewerbe.actions';
import { initialWettbewerb } from '../wettbewerbe.model';

@Component({
	selector: 'mka-wettbewerbe-list',
	templateUrl: './wettbewerbe-list.component.html',
	styleUrls: ['./wettbewerbe-list.component.css']
})
export class WettbewerbeListComponent implements OnInit {

	devMode = !environment.production;
	wettbewerbe$ = this.store.select(wettbewerbe);


	constructor(private store: Store<AppState>, private router: Router) { }

	ngOnInit(): void {
	}

	addWettbewerb() {
		this.store.dispatch(createNewWettbewerb({wettbewerb: initialWettbewerb}));
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/neu');
	}

	gotoDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
