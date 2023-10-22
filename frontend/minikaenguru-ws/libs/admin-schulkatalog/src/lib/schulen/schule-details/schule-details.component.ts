import { Component, Input, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { Subscription } from "rxjs";
import { Land, Ort, Schule } from "../../admin-katalog.model";


@Component({
    selector: 'mka-schule-details',
    templateUrl: './schule-details.component.html',
    styleUrls: ['./schule-details.component.css']
})
export class SchuleDetailsComponent implements OnInit, OnDestroy {

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;

    #schule: Schule | undefined;
    #schuleSubscription = new Subscription();

    ngOnInit(): void {

        this.#schuleSubscription = this.katalogFacade.schule$.subscribe((schule) => {
            if (schule) {
                this.#schule = schule;
            }
        });
    }

    ngOnDestroy(): void {
        this.#schuleSubscription.unsubscribe();
    }

    schuleUmbenennenDisabled(): boolean {
        return this.#schule === undefined;
    }

    schuleUmbenennen(): void {
        if (this.#schule) {
            this.katalogFacade.startRenameSchule(this.#schule);
        }
    }

    gotoSchulkatalog(): void {
        this.katalogFacade.navigateToSchulkatalog();
    }

}