import { Component, inject } from "@angular/core";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";


@Component({
    selector: 'mka-schulen',
    templateUrl: './schulen-list.component.html',
    styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent {

    katalogFacade = inject(AdminSchulkatalogFacade);

    schulen$ = this.katalogFacade.schulen$;

}