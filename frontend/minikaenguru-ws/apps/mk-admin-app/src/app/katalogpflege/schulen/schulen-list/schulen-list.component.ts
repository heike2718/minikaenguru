import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';

@Component({
	selector: 'mka-schulen-list',
	templateUrl: './schulen-list.component.html',
	styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent implements OnInit {

	schulen$ = this.katalogFacade.schulen$;
	selectedKatalogItem$ = this.katalogFacade.selectedKatalogItem$;

	inputValue: string;

	constructor(private katalogFacade: KatalogpflegeFacade, private router: Router) { }

	ngOnInit(): void {
		this.inputValue = '';
	}


	addSchule() {
		// this.katalogFacade.createNewLand();
		// this.router.navigateByUrl('/katalogpflege/land-editor/neu');
	}

	gotoLaender(): void {
		this.katalogFacade.resetSelection();
		this.router.navigateByUrl('/katalogpflege/laender');
	}

	gotoOrte(): void {
		this.katalogFacade.resetSelection();
		this.router.navigateByUrl('/katalogpflege/orte');
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

		this.katalogFacade.searchKatalogItems('SCHULE', this.inputValue.trim());

	}

	clearRearchResults(): void {

		this.inputValue = '';
		this.katalogFacade.clearRearchResults();

	}
}
