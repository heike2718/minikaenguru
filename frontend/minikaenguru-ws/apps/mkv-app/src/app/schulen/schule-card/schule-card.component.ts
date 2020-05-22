import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { selectSchule } from '../+state/schulen.actions';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	schule: Schule;


	constructor(private router: Router, private store: Store<AppState>) { }

	ngOnInit(): void {
	}


	selectSchule(): void {
		// TODO: hier die action loadSchule dispatchen, die im schulen.effects dann die schule läd und schuleSelected dispatched
		this.store.dispatch(selectSchule({ schule: this.schule }));
		this.router.navigateByUrl('/schulen/schule-dashboard');
	}

}
