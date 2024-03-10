import { Component, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { Subscribable, Subscription } from "rxjs";


@Component({
    selector: 'mka-schulen',
    templateUrl: './schulen-list.component.html',
    styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent implements OnInit, OnDestroy {

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);
    devMode = this.#config.devmode;

    #ortSubscription: Subscription = new Subscription();
    #schulenSubscription: Subscription = new Subscription();

    #anzahlSchulenImOrt = 0;
    anzahlTreffer = 0;

    ngOnInit(): void {

        this.#ortSubscription = this.katalogFacade.ort$.subscribe((ort) => {
            if (ort) {
                this.#anzahlSchulenImOrt = ort.anzahlKinder;
            }
        });

        this.#schulenSubscription = this.katalogFacade.schulen$.subscribe((schulen) => this.anzahlTreffer = schulen.length);

    }

    ngOnDestroy(): void {
        this.#ortSubscription.unsubscribe();
        this.#schulenSubscription.unsubscribe();
    }

    showAnzahlTreffer(): boolean {

        return this.#anzahlSchulenImOrt !== this.anzahlTreffer;
    }

}