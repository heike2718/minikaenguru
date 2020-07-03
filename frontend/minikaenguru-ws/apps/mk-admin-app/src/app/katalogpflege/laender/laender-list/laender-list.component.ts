import { Component, OnInit } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { Router } from '@angular/router';

@Component({
	selector: 'mka-laender-list',
	templateUrl: './laender-list.component.html',
	styleUrls: ['./laender-list.component.css']
})
export class LaenderListComponent implements OnInit {

	laender$ = this.katalogFacade.laender$;

	constructor(private katalogFacade: KatalogpflegeFacade, private router: Router) { }

	ngOnInit(): void {
	}

	addLand() {
		// this.wettbewerbFacade.createNewWettbewerb();
		// this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/neu');
	}

	gotoKataloge(): void {
		this.router.navigateByUrl('/katalogpflege');
	}

	gotoDashboard() {
		this.router.navigateByUrl('/dashboard');
	}

}
