import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { allSchulen } from '../+state/schulen.selectors';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-schulen-list',
	templateUrl: './schulen-list.component.html',
	styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent implements OnInit {

	devMode = !environment.production;

	schulen$ = this.appStore.select(allSchulen);

	constructor(private appStore: Store<AppState>, private router: Router) {
	}

	ngOnInit(): void {
	}

	addSchule(): void {
		console.log('hier neue Schule auswählen lassen')
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
