import { Component, Input, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { Subscription } from "rxjs";
import { Land, Ort } from "../../admin-katalog.model";
import { tap } from "rxjs/operators";
import { kuerzel } from "../../+state/admin-katalog.selectors";


@Component({
    selector: 'mka-ort-details',
    templateUrl: './ort-details.component.html',
    styleUrls: ['./ort-details.component.css']
})
export class OrtDetailsComponent implements OnInit, OnDestroy {

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;
    neueSchuleDisabled = false;
    

    #ort!: Ort;
    #ortSubscription = new Subscription();
    #kuerzelSubscription = new Subscription();

    #neueSchuleClicked = false;


    ngOnInit(): void {

        this.#ortSubscription = this.katalogFacade.ort$.subscribe((ort) => this.#ort = ort);

        this.#kuerzelSubscription = this.katalogFacade.kuerzel$.pipe(
            tap((kuerzel) => {
                if (this.#neueSchuleClicked) {
                    this.#neueSchuleClicked = false;
                    this.katalogFacade.startCreateSchuleInOrt(this.#ort, kuerzel);
                    this.neueSchuleDisabled = false;
                }
            })
        ).subscribe();

    }

    ngOnDestroy(): void {
        this.#ortSubscription.unsubscribe();
        this.#kuerzelSubscription.unsubscribe();
    }

    neueSchule(): void {
        this.neueSchuleDisabled = true;
        this.katalogFacade.triggerCreateKuerzel();
    }

}