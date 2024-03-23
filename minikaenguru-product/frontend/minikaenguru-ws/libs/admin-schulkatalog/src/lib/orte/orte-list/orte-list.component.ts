import { Component, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { Subscription } from "rxjs";


@Component({
    selector: 'mka-orte',
    templateUrl: './orte-list.component.html',
    styleUrls: ['./orte-list.component.css']
})
export class OrteListComponent implements OnInit, OnDestroy {

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;

    #landSubscription: Subscription = new Subscription();
    #orteSubscription: Subscription = new Subscription();

    #anzahlOrteImLand = 0;
    #anzahlTreffer = 0;

    ngOnInit(): void {

        this.#orteSubscription = this.katalogFacade.orte$.subscribe((orte) => this.#anzahlTreffer = orte.length);

        this.#landSubscription = this.katalogFacade.land$.subscribe ((land) => {
            if (land) {
                this.#anzahlOrteImLand = land.anzahlKinder;
            }
        });
        
    }

    ngOnDestroy(): void {
        this.#orteSubscription.unsubscribe();
        this.#landSubscription.unsubscribe();
    }

    showAnzahlTreffer(): boolean {
        return this.#anzahlOrteImLand !== this.#anzahlTreffer;
    }

}