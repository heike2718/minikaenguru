import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { VersandauftraegeFacade } from '../versandauftraege.facade';

@Component({
	selector: 'mka-versandauftraege-list',
	templateUrl: './versandauftraege-list.component.html',
	styleUrls: ['./versandauftraege-list.component.css']
})
export class VersandauftraegeListComponent implements OnInit {

	devMode = environment.envName === 'DEV'

	constructor(private router: Router,
		public versandauftraegeFacade: VersandauftraegeFacade) { }

	ngOnInit(): void {
		this.versandauftraegeFacade.loadVersandauftraege();
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
