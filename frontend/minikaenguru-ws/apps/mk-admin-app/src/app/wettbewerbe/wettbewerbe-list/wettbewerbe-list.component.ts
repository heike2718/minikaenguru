import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { wettbewerbe } from '../+state/wettbewerbe.selectors';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

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
		console.log('jetzt neuen Wettbewerb anlegen');
	}

	gotoDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
