import { Component, inject } from '@angular/core';
import { KatalogpflegeFacade } from './katalogpflege.facade';
import { AdminSchulkatalogFacade } from 'libs/admin-schulkatalog/src/lib/admin-schulkatalog.facade';

@Component({
	selector: 'mka-katalogpflege',
	templateUrl: './katalogpflege.component.html',
	styleUrls: ['./katalogpflege.component.css']
})
export class KatalogpflegeComponent {

	#katalogFacade = inject(KatalogpflegeFacade);
	#adminKatalogFacade = inject(AdminSchulkatalogFacade);


	selectLaender(): void {
		this.#adminKatalogFacade.loadLaender();
	}

	selectOrte(): void {
		this.#katalogFacade.selectKatalogpflegeTyp('ORT');
	}

	selectSchulen(): void {
		this.#katalogFacade.selectKatalogpflegeTyp('SCHULE');
	}
}
