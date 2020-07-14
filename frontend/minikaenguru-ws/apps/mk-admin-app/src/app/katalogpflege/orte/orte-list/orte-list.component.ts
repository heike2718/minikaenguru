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

	addOrtUndSchule() {
		this.katalogFacade.switchToCreateNeueSchuleEditor();
	}

	gotoKataloge(): void {
		this.katalogFacade.switchToKataloge();
	}

	gotoDashboard() {
		this.katalogFacade.switchToDashboard();
	}

	startSearch(): void {

		this.katalogFacade.searchKatalogItems('ORT', this.inputValue.trim());

	}

	clearRearchResults(): void {

		this.inputValue = '';
		this.katalogFacade.clearSearchResults();

	}
}
