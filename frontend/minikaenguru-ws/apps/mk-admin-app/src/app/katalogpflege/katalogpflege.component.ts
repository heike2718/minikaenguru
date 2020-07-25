import { Component, OnInit, OnDestroy } from '@angular/core';
import { KatalogpflegeFacade } from './katalogpflege.facade';
import { environment} from '../../environments/environment';

@Component({
	selector: 'mka-katalogpflege',
	templateUrl: './katalogpflege.component.html',
	styleUrls: ['./katalogpflege.component.css']
})
export class KatalogpflegeComponent implements OnInit, OnDestroy {

	uploadUrl = environment.apiUrl + '/upload/schulen/csv';

	constructor(private katalogFacade: KatalogpflegeFacade) { }

	ngOnInit(): void {


	}

	ngOnDestroy(): void {

	}

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
