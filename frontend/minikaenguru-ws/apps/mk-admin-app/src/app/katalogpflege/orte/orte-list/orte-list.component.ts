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

	inputValue: string;

	constructor(private katalogFacade: KatalogpflegeFacade, private router: Router) { }

	ngOnInit(): void {
		this.inputValue = '';
	}

	addOrt() {
		// this.wettbewerbFacade.createNewWettbewerb();
		// this.router.navigateByUrl('/wettbewerbe/wettbewerb-editor/neu');
	}

	gotoLaender(): void {
		this.katalogFacade.resetSelection();
		this.router.navigateByUrl('/katalogpflege/laender');
	}

	gotoKataloge(): void {
		this.katalogFacade.resetSelection();
		this.router.navigateByUrl('/katalogpflege');
	}

	gotoDashboard() {
		this.katalogFacade.resetSelection();
		this.router.navigateByUrl('/dashboard');
	}

	startSearch(): void {

		this.katalogFacade.searchKatalogItems('ORT', this.inputValue.trim());

	}

	clearRearchResults(): void {

		this.inputValue = '';
		this.katalogFacade.clearRearchResults();

	}
}
