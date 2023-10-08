import { Component, Input, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { Subscription } from "rxjs";
import { Land, Ort } from "../../admin-katalog.model";


@Component({
    selector: 'mka-ort-details',
    templateUrl: './ort-details.component.html',
    styleUrls: ['./ort-details.component.css']
})
export class OrtDetailsComponent implements OnInit, OnDestroy {

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;

    #ort: Ort | undefined;
    #ortSubscription = new Subscription();



    ngOnInit(): void {

        this.#ortSubscription = this.katalogFacade.ort$.subscribe((ort) => {
            if (ort) {
                this.#ort = ort;
            }
        });
    }

    ngOnDestroy(): void {
        this.#ortSubscription.unsubscribe();
    }

}