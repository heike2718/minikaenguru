import { Component, OnInit } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { Router } from '@angular/router';

@Component({
	selector: 'mka-orte-list',
	templateUrl: './orte-list.component.html',
	styleUrls: ['./orte-list.component.css']
})
export class OrteListComponent implements OnInit {

	orte$ = this.katalogFacade.orte$;
	selectedKatalogItem$ = this.katalogFacade.selectedKatalogItem$;

	constructor(private katalogFacade: KatalogpflegeFacade, private router: Router) { }

	ngOnInit(): void {
	}

	addOrt() {
		// this.wettbewerbFacade.createNewWettbewerb();
		// this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/neu');
	}

	gotoKataloge(): void {
		this.katalogFacade.resetSelection();
		this.router.navigateByUrl('/katalogpflege');
	}

	gotoDashboard() {
		this.katalogFacade.resetSelection();
		this.router.navigateByUrl('/dashboard');
	}
}
