import { Component, Input, inject } from "@angular/core";
import { Ort, Schule } from "../../admin-katalog.model";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";

@Component({
	selector: 'mka-schule',
	templateUrl: './schule.component.html',
	styleUrls: ['./schule.component.css']
})
export class SchuleComponent {

    @Input()
    schule!: Schule;

    #config = inject(AdminSchulkatalogConfigService);
	#katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;

    selectSchule(): void {
        this.#katalogFacade.selectSchule(this.schule);        
    }
}