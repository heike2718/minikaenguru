import { Component, Input, inject } from "@angular/core";
import { Ort } from "../../admin-katalog.model";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";

@Component({
	selector: 'mka-ort',
	templateUrl: './ort.component.html',
	styleUrls: ['./ort.component.css']
})
export class OrtComponent {

    @Input()
    ort!: Ort;

    #config = inject(AdminSchulkatalogConfigService);
	#katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;

    selectOrt(): void {  
        this.#katalogFacade.selectOrt(this.ort);      
    }

}