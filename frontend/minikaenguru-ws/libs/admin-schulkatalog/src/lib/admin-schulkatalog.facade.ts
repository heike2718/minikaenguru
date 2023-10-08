import { Injectable, inject } from "@angular/core";
import { Store } from "@ngrx/store";
import { AdminSchulkatalogState } from "./+state/admin-katalog.reducer";
import * as SchulkatalogSelectors from './+state/admin-katalog.selectors';
import * as SchulkatalogActions from './+state/admin-katalog.actions';
import { KuerzelResponseDto, Land, Ort, Schule, SchuleEditorModel, SchulePayload, initialSchuleEditorModel, initialSchulePayload } from "./admin-katalog.model";
import { Observable } from "rxjs";
import { filterDefined } from "@minikaenguru-ws/shared/util-mk";

@Injectable({
    providedIn: 'root'
})
export class AdminSchulkatalogFacade {

    #store = inject(Store<AdminSchulkatalogState>);

    laender$: Observable<Land[]> = this.#store.select(SchulkatalogSelectors.laender);
    land$: Observable<Land | undefined> = this.#store.select(SchulkatalogSelectors.selectedLand);

    orte$: Observable<Ort[]> = this.#store.select(SchulkatalogSelectors.orte);
    orteGeladen$: Observable<boolean> = this.#store.select(SchulkatalogSelectors.orteGeladen);
    ort$: Observable<Ort> = this.#store.select(SchulkatalogSelectors.selectedOrt).pipe(o => filterDefined(o));

    schulen$: Observable<Schule[]> = this.#store.select(SchulkatalogSelectors.schulen);
    schulenGeladen$: Observable<boolean> = this.#store.select(SchulkatalogSelectors.schulenGeladen);
    schule$: Observable<Schule | undefined> = this.#store.select(SchulkatalogSelectors.selectedSchule);

    schuleEditorModel$: Observable<SchuleEditorModel> = this.#store.select(SchulkatalogSelectors.schuleEditorModel).pipe(i => filterDefined(i));

    kuerzel$: Observable<KuerzelResponseDto> = this.#store.select(SchulkatalogSelectors.kuerzel).pipe(k => filterDefined(k));

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

    triggerCreateKuerzel(): void {

        this.#store.dispatch(SchulkatalogActions.createKuerzel());
    }

    startCreateSchuleInOrt(ort: Ort, kuerzel: KuerzelResponseDto): void {

        const schulePayload: SchulePayload = {
            ...initialSchulePayload,
            kuerzel: kuerzel.kuerzelSchule,
            kuerzelLand: ort.land.kuerzel,
            nameLand: ort.land.name
        };

        const schuleEditorModel: SchuleEditorModel = {
            ...initialSchuleEditorModel,
            schulePayload: schulePayload,
            nameOrtDisabled: true,
            kuerzelLandDisabled: true,
            nameLandDisabled: true    
        }

    }

    startCreateSchuleInLand(land: Land, kuerzel: KuerzelResponseDto): void {

        const schulePayload: SchulePayload = {
            ...initialSchulePayload,
            kuerzel: kuerzel.kuerzelSchule,
            kuerzelOrt: kuerzel.kuerzelOrt,
            kuerzelLand: land.kuerzel,
            nameLand: land.name
        };

        const schuleEditorModel: SchuleEditorModel = {
            ...initialSchuleEditorModel,
            schulePayload: schulePayload,
            kuerzelLandDisabled: true,
            nameLandDisabled: true    
        }
    }

    createSchule(schulePayload: SchulePayload): void {

    }

    renameSchule(schulePayload: SchulePayload): void {
        
    }

    onLogout(): void {
        this.#store.dispatch(SchulkatalogActions.resetState());
    }
}