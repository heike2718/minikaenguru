import { Component } from '@angular/core';
import { KatalogpflegeFacade } from './katalogpflege.facade';

@Component({
	selector: 'mka-katalogpflege',
	templateUrl: './katalogpflege.component.html',
	styleUrls: ['./katalogpflege.component.css']
})
export class KatalogpflegeComponent {

	constructor(private katalogFacade: KatalogpflegeFacade) { }

	selectLaender(): void {
		this.katalogFacade.selectKatalogpflegeTyp('LAND');
	}

	selectOrte(): void {
		this.katalogFacade.selectKatalogpflegeTyp('ORT');
	}

	selectSchulen(): void {
		this.katalogFacade.selectKatalogpflegeTyp('SCHULE');
	}
}
