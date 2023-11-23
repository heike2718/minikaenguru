import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { Observable, of } from "rxjs";
import { KatalogitemResponseDto, KuerzelResponseDto, Land, LandPayload, Ort, OrtPayload, OrteSucheResult, SchuleEditorModel, SchuleEditorModelAndKuerzel, SchulePayload, SchulenSucheResult } from "./admin-katalog.model";
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

    loadOrte(land: Land): Observable<OrteSucheResult> {

        const url = this.#config.baseUrl + '/kataloge/laender/' + land.kuerzel + '/orte';
        const obs$ = this.#httpClient.get(url).pipe(
            map(body => body as ResponsePayload),
            map((payload) => this.#toOrtSucheResult(land, payload.data)),
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    loadSchulen(ort: Ort): Observable<SchulenSucheResult> {

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

    findOrte(land: Land, suchstring: string): Observable<OrteSucheResult> {

        const url = this.#config.baseUrl + '/kataloge/suche/laender/' + land.kuerzel + '/orte';

        let params = new HttpParams().set('search', suchstring);
        const headers = new HttpHeaders().set('Accept', 'application/json');

        const obs$ = this.#httpClient.get(url, { headers, params }).pipe(
            map(body => body as ResponsePayload),
            map((payload) => this.#toOrtSucheResult(land, payload.data))
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    findSchulen(ort: Ort, suchstring: string): Observable<SchulenSucheResult> {

        const url = this.#config.baseUrl + '/kataloge/suche/orte/' + ort.kuerzel + '/schulen';

        let params = new HttpParams().set('search', suchstring);
        const headers = new HttpHeaders().set('Accept', 'application/json');

        const obs$ = this.#httpClient.get(url, { headers, params }).pipe(
            map(body => body as ResponsePayload),
            map((payload) => this.#toSchuleSucheResult(ort, payload.data))
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    createSchule(schulePayload: SchulePayload): Observable<SchulePayload> {

        const url = this.#config.baseUrl + '/kataloge/schulen';

        const obs$ = this.#httpClient.post(url, schulePayload).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data as SchulePayload)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);

    }

    renameSchule(schulePayload: SchulePayload): Observable<SchulePayload> {

        const url = this.#config.baseUrl + '/kataloge/schulen';

        const obs$ = this.#httpClient.put(url, schulePayload).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data as SchulePayload)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);

    }

    updateLand(landPayload: LandPayload): Observable<LandPayload> {

        const url = this.#config.baseUrl + '/kataloge/laender';

        const obs$ = this.#httpClient.put(url, landPayload).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data as LandPayload)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    updateOrt(ortPayload: OrtPayload): Observable<OrtPayload> {

        const url = this.#config.baseUrl + '/kataloge/orte';

        const obs$ = this.#httpClient.put(url, ortPayload).pipe(
            map(body => body as ResponsePayload),
            map(payload => payload.data as OrtPayload)
        );

        return this.#loadingIndicator.showLoaderUntilCompleted(obs$);
    }

    #toOrtSucheResult(land: Land, orte: any | undefined): OrteSucheResult {

        let theOrte: KatalogitemResponseDto[] = [];

        if (orte) {
            theOrte = orte
        }


        const result: OrteSucheResult = {
            land: land,
            orte: theOrte
        }

        return result;
    }

    #toSchuleSucheResult(ort: Ort, schulen: any | undefined): SchulenSucheResult {

        let theSchulen: KatalogitemResponseDto[] = [];

        if (schulen) {
            theSchulen = schulen
        }


        const result: SchulenSucheResult = {
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
