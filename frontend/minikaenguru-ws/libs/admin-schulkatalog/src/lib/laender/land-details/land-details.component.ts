import { Component, Input, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { Subscription } from "rxjs";
import { Land } from "../../admin-katalog.model";


@Component({
    selector: 'mka-land-details',
    templateUrl: './land-details.component.html',
    styleUrls: ['./land-details.component.css']
})
export class LandDetailsComponent implements OnInit, OnDestroy {

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;

    #land: Land | undefined;
    #landSubscription = new Subscription();



    ngOnInit(): void {

        this.#landSubscription = this.katalogFacade.land$.subscribe((land) => {
            if (land) {
                this.#land = land;
            }
        });
    }

    ngOnDestroy(): void {
        this.#landSubscription.unsubscribe();
    }

}