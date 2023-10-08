import { Component, inject } from "@angular/core";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";


@Component({
    selector: 'mka-orte',
    templateUrl: './orte-list.component.html',
    styleUrls: ['./orte-list.component.css']
})
export class OrteListComponent {

    katalogFacade = inject(AdminSchulkatalogFacade);

    orte$ = this.katalogFacade.orte$;

}