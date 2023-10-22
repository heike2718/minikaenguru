import { Component, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { Subscription } from "rxjs";
import { Land } from "../../admin-katalog.model";
import { tap } from "rxjs/operators";


@Component({
    selector: 'mka-land-details',
    templateUrl: './land-details.component.html',
    styleUrls: ['./land-details.component.css']
})
export class LandDetailsComponent implements OnInit, OnDestroy {

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;

    neueSchuleDisabled = false;
    #neueSchuleClicked = false;

    #land: Land | undefined;
    #landSubscription = new Subscription();
    #kuerzelSubscription = new Subscription();


    suchstring: string = '';

    ngOnInit(): void {

        this.#landSubscription = this.katalogFacade.land$.subscribe((land) => {
            if (land) {
                this.#land = land;
            }
        });

        this.#kuerzelSubscription = this.katalogFacade.kuerzel$.pipe(
            tap((kuerzel) => {
                if (this.#land && this.#neueSchuleClicked) {
                    this.#neueSchuleClicked = false;
                    this.katalogFacade.startCreateOrtUndSchuleInLand(this.#land, kuerzel);
                    this.neueSchuleDisabled = false;
                }
            })
        ).subscribe();
    }

    ngOnDestroy(): void {
        this.#landSubscription.unsubscribe();
        this.#kuerzelSubscription.unsubscribe();
    }

    startSearch(): void {

        if (this.#land && this.suchstring.trim().length > 0) {
            this.katalogFacade.sucheOrteInLand(this.#land, this.suchstring.trim());
        }

    }

    buttonSucheDisabled(): boolean {

        if (!this.#land) {
            return true;
        }

        return this.suchstring.trim().length === 0;
    }

    neueSchule(): void {

        if (!this.#land) {
            return;
        }

        this.neueSchuleDisabled = true;
        this.#neueSchuleClicked = true;
        this.katalogFacade.triggerCreateKuerzel();
    }

    editLand(): void {

        if (this.#land) {
            this.katalogFacade.startEditLand({...this.#land});
        }

    }

    gotoSchulkatalog(): void {
        this.katalogFacade.navigateToSchulkatalog();
    }

}