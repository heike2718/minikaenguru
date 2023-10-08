import { Component, OnInit } from '@angular/core';
import { KatalogpflegeFacade } from '../../katalogpflege.facade';

@Component({
	selector: 'mka-laender-list',
	templateUrl: './deprecated-laender-list.component.html',
	styleUrls: ['./deprecated-laender-list.component.css']
})
export class DeprecatedLaenderListComponent implements OnInit {

	laender$ = this.katalogFacade.laender$;

	constructor(private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {
	}

	addLandOrtUndSchule() {
		this.katalogFacade.switchToCreateNeueSchuleEditor();
	}

	gotoKataloge(): void {
		this.katalogFacade.switchToKataloge();
	}

	gotoDashboard() {
		this.katalogFacade.switchToDashboard();
	}

}
