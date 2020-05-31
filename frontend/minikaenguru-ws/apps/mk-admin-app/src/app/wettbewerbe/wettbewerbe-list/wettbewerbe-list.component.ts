import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { WettbewerbFacade } from '../../services/wettbewerb.facade';
import { Observable } from 'rxjs';
import { Wettbewerb } from '../wettbewerbe.model';

@Component({
	selector: 'mka-wettbewerbe-list',
	templateUrl: './wettbewerbe-list.component.html',
	styleUrls: ['./wettbewerbe-list.component.css']
})
export class WettbewerbeListComponent implements OnInit {

	devMode = !environment.production;
	wettbewerbe$: Observable<Wettbewerb[]> = this.wettbewerbFacade.wettbewerbe$;


	constructor(private wettbewerbFacade: WettbewerbFacade, private router: Router) { }

	ngOnInit(): void {
	}

	addWettbewerb() {
		this.wettbewerbFacade.createNewWettbewerb();
		this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/neu');
	}

	gotoDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
