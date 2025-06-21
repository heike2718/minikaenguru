import { Injectable, inject } from "@angular/core";
import { Store } from "@ngrx/store";
import { AdminSchulkatalogState } from "./+state/admin-katalog.reducer";
import * as SchulkatalogSelectors from './+state/admin-katalog.selectors';
import * as SchulkatalogActions from './+state/admin-katalog.actions';
import { KuerzelResponseDto, Land, LandPayload, Ort, OrtPayload, Schule, SchuleEditorModel, SchulePayload, initialSchuleEditorModel, initialSchulePayload } from "./admin-katalog.model";
import { Observable } from "rxjs";
import { filterDefined } from "@minikaenguru-ws/shared/util-mk";
import { Router } from "@angular/router";
import { filter } from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class AdminSchulkatalogFacade {

    #store = inject(Store<AdminSchulkatalogState>);
    #router = inject(Router);

    laender$: Observable<Land[]> = this.#store.select(SchulkatalogSelectors.laender);
    land$: Observable<Land | undefined> = this.#store.select(SchulkatalogSelectors.selectedLand);

    orte$: Observable<Ort[]> = this.#store.select(SchulkatalogSelectors.orte);
    orteGeladen$: Observable<boolean> = this.#store.select(SchulkatalogSelectors.orteGeladen);
    ort$: Observable<Ort> = this.#store.select(SchulkatalogSelectors.selectedOrt).pipe(o => filterDefined(o));

    schulen$: Observable<Schule[]> = this.#store.select(SchulkatalogSelectors.schulen);
    schulenGeladen$: Observable<boolean> = this.#store.select(SchulkatalogSelectors.schulenGeladen);
    schule$: Observable<Schule | undefined> = this.#store.select(SchulkatalogSelectors.selectedSchule);

    landPayload$: Observable<LandPayload> = this.#store.select(SchulkatalogSelectors.landPayload).pipe(p => filterDefined(p));
    ortPayload$: Observable<OrtPayload> = this.#store.select(SchulkatalogSelectors.ortPayload).pipe(p => filterDefined(p));
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

    sucheSchulenInOrt(ort: Ort, suchstring: string): void {

        this.#store.dispatch(SchulkatalogActions.findSchulen({ort, suchstring}));

    }

    sucheOrteInLand(land: Land, suchstring: string): void {

        this.#store.dispatch(SchulkatalogActions.findOrte({ land, suchstring }));

    }

    triggerCreateKuerzel(): void {

        this.#store.dispatch(SchulkatalogActions.createKuerzel());
    }

    startCreateSchuleInOrt(ort: Ort, kuerzelResponseDto: KuerzelResponseDto): void {

        const schulePayload: SchulePayload = {
            ...initialSchulePayload,
            kuerzel: kuerzelResponseDto.kuerzelSchule,
            kuerzelOrt: ort.kuerzel,
            nameOrt: ort.name,
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

        this.#store.dispatch(SchulkatalogActions.startEditSchule({ schuleEditorModel }));
    }

    startCreateOrtUndSchuleInLand(land: Land, kuerzel: KuerzelResponseDto): void {

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

        this.#store.dispatch(SchulkatalogActions.startEditSchule({ schuleEditorModel }));
    }

    startCreateLandOrtUndSchule(kuerzel: KuerzelResponseDto): void {

        const schulePayload: SchulePayload = {
            ...initialSchulePayload,
            kuerzel: kuerzel.kuerzelSchule,
            kuerzelOrt: kuerzel.kuerzelOrt
        };

        const schuleEditorModel: SchuleEditorModel = {
            ...initialSchuleEditorModel,
            schulePayload: schulePayload
        };

        this.#store.dispatch(SchulkatalogActions.startEditSchule({ schuleEditorModel }));
    }

    createSchule(schulePayload: SchulePayload): void {

        this.#store.dispatch(SchulkatalogActions.createSchule({ schulePayload }));

    }

    startRenameSchule(schule: Schule): void {

        const schulePayload: SchulePayload = {
            kuerzel: schule.kuerzel,
            kuerzelLand: schule.ort.land.kuerzel,
            kuerzelOrt: schule.ort.kuerzel,
            name: schule.name,
            nameLand: schule.land.name,
            nameOrt: schule.ort.name
        };

        const schuleEditorModel: SchuleEditorModel = {
            ...initialSchuleEditorModel,
            schulePayload: schulePayload,
            kuerzelLandDisabled: true,
            nameLandDisabled: true,
            modusCreate: false,
            nameOrtDisabled: true
        }

        this.#store.dispatch(SchulkatalogActions.startEditSchule({ schuleEditorModel }));

    }

    updateSchule(schulePayload: SchulePayload): void {

        this.#store.dispatch(SchulkatalogActions.updateSchule({ schulePayload }));

    }

    startEditOrt(ortPayload: OrtPayload): void {
        this.#store.dispatch(SchulkatalogActions.startEditOrt({ ortPayload }));
    }

    updateOrt(ortPayload: OrtPayload): void {

        this.#store.dispatch(SchulkatalogActions.updateOrt({ ortPayload }));

    }

    startEditLand(landPayload: LandPayload): void {
        this.#store.dispatch(SchulkatalogActions.startEditLand({ landPayload }));
    }

    updateLand(landPayload: LandPayload): void {

        this.#store.dispatch(SchulkatalogActions.updateLand({ landPayload }));

    }

    navigateToSchulkatalog(): void {
        this.#store.dispatch(SchulkatalogActions.resetState());
        this.#router.navigateByUrl('schulkatalog/laender');
    }

    onLogout(): void {
        this.#store.dispatch(SchulkatalogActions.resetState());
    }
}