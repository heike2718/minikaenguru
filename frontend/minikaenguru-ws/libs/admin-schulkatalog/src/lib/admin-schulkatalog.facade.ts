import { Injectable, inject } from "@angular/core";
import { Store } from "@ngrx/store";
import { AdminSchulkatalogState } from "./+state/admin-katalog.reducer";
import * as SchulkatalogSelectors from './+state/admin-katalog.selectors';
import * as SchulkatalogActions from './+state/admin-katalog.actions';
import { Land, Ort, Schule } from "./admin-katalog.model";
import { Observable } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class AdminSchulkatalogFacade {

    #store = inject(Store<AdminSchulkatalogState>);

    laender$: Observable<Land[]> = this.#store.select(SchulkatalogSelectors.laender);
    land$: Observable<Land | undefined> = this.#store.select(SchulkatalogSelectors.selectedLand);
    
    orte$: Observable<Ort[]> = this.#store.select(SchulkatalogSelectors.orte);
    orteGeladen$: Observable<boolean> = this.#store.select(SchulkatalogSelectors.orteGeladen);
    ort$: Observable<Ort | undefined> = this.#store.select(SchulkatalogSelectors.selectedOrt);

    schulen$: Observable<Schule[]> = this.#store.select(SchulkatalogSelectors.schulen);
    schulenGeladen$: Observable<boolean> = this.#store.select(SchulkatalogSelectors.schulenGeladen);
    schule$: Observable<Schule | undefined> = this.#store.select(SchulkatalogSelectors.selectedSchule);

    loadLaender(): void {

        this.#store.dispatch(SchulkatalogActions.loadLaender());
    }

    selectLand(land: Land): void {

        this.#store.dispatch(SchulkatalogActions.landSelected({ land }));

        if (land.anzahlKinder < 26) {
            this.#store.dispatch(SchulkatalogActions.loadOrte({ land }));
        }
    }

    selectOrt(ort: Ort): void {

        this.#store.dispatch(SchulkatalogActions.ortSelected({ ort }));

        if (ort.anzahlKinder < 26) {
            this.#store.dispatch(SchulkatalogActions.loadSchulen({ ort }));
        }
    }

    selectSchule(schule: Schule): void {

        this.#store.dispatch(SchulkatalogActions.schuleSelected({ schule }));
    }

    onLogout(): void {
        this.#store.dispatch(SchulkatalogActions.resetState());
    }
}