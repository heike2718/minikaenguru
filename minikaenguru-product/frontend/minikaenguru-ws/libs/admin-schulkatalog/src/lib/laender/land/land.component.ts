import { Component, Input, inject } from "@angular/core";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { Land } from "../../admin-katalog.model";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";


@Component({
	selector: 'mka-land',
	templateUrl: './land.component.html',
	styleUrls: ['./land.component.css']
})
export class LandComponent {

	#config = inject(AdminSchulkatalogConfigService);
	#katalogFacade = inject(AdminSchulkatalogFacade);

	devMode = this.#config.devmode;

	@Input()
	land!: Land;

	selectLand(): void {
		this.#katalogFacade.selectLand(this.land);
	}

}