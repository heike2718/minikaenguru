import { Component, OnInit, inject } from '@angular/core';
import { AdminSchulkatalogFacade } from '../../admin-schulkatalog.facade';

@Component({
	selector: 'mka-laender-list',
	templateUrl: './laender-list.component.html',
	styleUrls: ['./laender-list.component.css']
})
export class LaenderListComponent implements OnInit {

	katalogFacade = inject(AdminSchulkatalogFacade);

	laender$ = this.katalogFacade.laender$;
	
	ngOnInit(): void {
	}
}
