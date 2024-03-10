import { Component, Input, OnDestroy, OnInit, inject } from "@angular/core";
import { AdminSchulkatalogConfigService } from "../../configuration/schulkatalog-config";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { BehaviorSubject, Subscription } from "rxjs";
import { Land, Ort, OrtPayload } from "../../admin-katalog.model";
import { debounceTime, distinctUntilChanged, filter, tap } from "rxjs/operators";
import { kuerzel } from "../../+state/admin-katalog.selectors";


@Component({
    selector: 'mka-ort-details',
    templateUrl: './ort-details.component.html',
    styleUrls: ['./ort-details.component.css']
})
export class OrtDetailsComponent implements OnInit, OnDestroy {

    searchFormInputValue!: string;
    #searchTerm!: BehaviorSubject<string>;

    #config = inject(AdminSchulkatalogConfigService);
    katalogFacade = inject(AdminSchulkatalogFacade);

    devMode = this.#config.devmode;
    neueSchuleDisabled = false;


    #ort!: Ort;
    #ortSubscription = new Subscription();
    #kuerzelSubscription = new Subscription();

    #neueSchuleClicked = false;


    ngOnInit(): void {

        this.#searchTerm = new BehaviorSubject<string>('');

        this.#searchTerm.pipe(
            debounceTime(1000),
            distinctUntilChanged(),
            filter(term => term.length > 2),
            tap(term => {
                this.#startSearch(term)
            })
        ).subscribe();

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
        this.#neueSchuleClicked = true;
        this.katalogFacade.triggerCreateKuerzel();
    }

    editOrt(): void {

        if (this.#ort) {

            const ortPayload: OrtPayload = {
                kuerzel: this.#ort.kuerzel,
                name: this.#ort.name,
                kuerzelLand: this.#ort.land.kuerzel,
                nameLand: this.#ort.land.name
            }

            this.katalogFacade.startEditOrt(ortPayload);
        }

    }

    gotoSchulkatalog(): void {
        this.katalogFacade.navigateToSchulkatalog();
    }

    onKeyup($event: any) {

		const value = $event.target.value;
		this.#searchTerm.next(value);
	}

    #startSearch(term: string): void {

        if (term.trim().length > 2) {
            this.katalogFacade.sucheSchulenInOrt(this.#ort, term.trim());
        }

    }

}