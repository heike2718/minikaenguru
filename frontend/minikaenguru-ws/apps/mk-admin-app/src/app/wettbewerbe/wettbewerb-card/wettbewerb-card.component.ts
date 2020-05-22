import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Wettbewerb } from '../wettbewerbe.model';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { selectWettbewerb } from '../+state/wettbewerbe.actions';

@Component({
	selector: 'mka-wettbewerb-card',
	templateUrl: './wettbewerb-card.component.html',
	styleUrls: ['./wettbewerb-card.component.css']
})
export class WettbewerbCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	wettbewerb: Wettbewerb;

	constructor(private router: Router, private store: Store<AppState>) { }

	ngOnInit(): void {
	}


	selectWettbewerb(): void {
		this.store.dispatch(selectWettbewerb({ wettbewerb: this.wettbewerb }));
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-dashboard');
	}

}
