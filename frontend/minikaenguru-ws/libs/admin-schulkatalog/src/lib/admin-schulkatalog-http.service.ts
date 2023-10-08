import { HttpClient } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable } from "rxjs";
import { KatalogitemResponseDto, KuerzelResponseDto, Land, Ort, OrtSucheResult, SchuleEditorModel, SchuleEditorModelAndKuerzel, SchulePayload, SchuleSucheResult } from "./admin-katalog.model";
import { LoadingIndicatorService } from '@minikaenguru-ws/shared/util-mk';
import { AdminSchulkatalogConfigService } from "./configuration/schulkatalog-config";
import { ResponsePayload } from "@minikaenguru-ws/common-messages";
import { map, switchMap } from "rxjs/operators";

@Injectable({
    providedIn: 'root'
})
export class AdminSchulkatalogHttpService {

    #httpClient = inject(HttpClient);
    #config = inject(AdminSchulkatalogConfigService);
    #loadingIndicator = inject(LoadingIndicatorService);

    loadLaender(): Observable<KatalogitemResponseDto[]> {

        const url = this.#config.baseUrl + '/kataloge/laender';

        const obs$ = this.#httpClient.get(url).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    loadOrte(land: Land): Observable<OrtSucheResult> {

        const url = this.#config.baseUrl + '/kataloge/laender/' + land.kuerzel + '/orte';
        const obs$ = this.#httpClient.get(url).pipe(
            map(body => body as ResponsePayload),
            map((payload) => this.#toOrtSucheResult(land, payload.data)),
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    loadSchulen(ort: Ort): Observable<SchuleSucheResult> {

        const url = this.#config.baseUrl + '/kataloge/orte/' + ort.kuerzel + '/schulen';
        const obs$ = this.#httpClient.get(url).pipe(
            map(body => body as ResponsePayload),
            map((payload) => this.#toSchuleSucheResult(ort, payload.data)),
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    getKuerzel(): Observable<KuerzelResponseDto> {


        const url = this.#config.baseUrl + '/kataloge/kuerzel';

        const obs$ = this.#httpClient.get(url).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    createSchule(schulePayload: SchulePayload): Observable<ResponsePayload> {

        const url = this.#config.baseUrl + '/kataloge/schulen';

        const obs$ = this.#httpClient.post(url, schulePayload).pipe(
            map(body => body as ResponsePayload)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);

    }

    renameSchule(schulePayload: SchulePayload): Observable<ResponsePayload> {

        const url = this.#config.baseUrl + '/kataloge/schulen';

        const obs$ = this.#httpClient.put(url, schulePayload).pipe(
            map(body => body as ResponsePayload)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);

    }

    #toOrtSucheResult(land: Land, orte: any | undefined): OrtSucheResult {

        let theOrte: KatalogitemResponseDto[] = [];

        if (orte) {
            theOrte = orte
        }


        const result: OrtSucheResult = {
            land: land,
            orte: theOrte
        }

        return result;
    }

    #toSchuleSucheResult(ort: Ort, schulen: any | undefined): SchuleSucheResult {

        let theSchulen: KatalogitemResponseDto[] = [];

        if (schulen) {
            theSchulen = schulen
        }


        const result: SchuleSucheResult = {
            ort: ort,
            schulen: theSchulen
        }

        return result;
    }

    #toSchuleEditorModelAndKuerzel(schuleEditorModel: SchuleEditorModel, kuerzel: any | undefined): SchuleEditorModelAndKuerzel {

        const theKuerzel: KuerzelResponseDto = kuerzel ? kuerzel : {
            kuerzelOrt: '',
            kuerzelSchule: ''
        };

        return {
            schuleEditorModel: schuleEditorModel,
            kuerzel: theKuerzel
        };

    }
}
