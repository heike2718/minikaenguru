import { Component, OnInit } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';
import { environment } from '../../../../environments/environment';

@Component({
	selector: 'mka-schulen-list',
	templateUrl: './schulen-list.component.html',
	styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent implements OnInit {

	devMode = environment.envName === 'DEV';

	schulen$ = this.katalogFacade.schulen$;
	selectedKatalogItem$ = this.katalogFacade.selectedKatalogItem$;

	inputValue: string = '';

	constructor(private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {
	}

	addSchule() {
		this.katalogFacade.switchToCreateNeueSchuleEditor();
	}

	gotoKataloge(): void {
		this.katalogFacade.switchToKataloge();

	}

	gotoDashboard() {
		this.katalogFacade.switchToDashboard();

	}

	startSearch(): void {

		this.katalogFacade.searchKatalogItems('SCHULE', this.inputValue.trim());

	}

	clearRearchResults(): void {
		this.inputValue = '';
		this.katalogFacade.clearSearchResults();

	}
}
