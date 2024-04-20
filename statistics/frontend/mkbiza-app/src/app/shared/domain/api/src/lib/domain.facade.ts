import { Injectable, inject } from "@angular/core";
import { domainActions, fromDomain } from '@mkbiza-app/domain-data';

import { Store, select } from "@ngrx/store";
import { Observable, Subscription, take } from "rxjs";
import { Klassenstufe, KlassenstufeGUIModel, WettbewerbDetailsGUIModel, WettbewerbOverview } from "@mkbiza-app/domain-model";
import { ChartData } from "chart.js";
import { filterDefined } from "./filter-defined";

@Injectable({
    providedIn: 'root'
})
export class DomainFacade {

    #store = inject(Store);

    #loadWettbewerbsdetailsSubscription = new Subscription();
    #loadKlassenstufeSubscription = new Subscription();

    wettbewerbIDs$: Observable<number[]> = this.#store.select(fromDomain.wettbewerbIDs);
    wettbewerbe$: Observable<WettbewerbOverview[]> = this.#store.select(fromDomain.wettbewerbe);
    jahreAnzahlKinder$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreAnzahlKinder).pipe(filterDefined);
    jahreMediane$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreMediane).pipe(filterDefined);
    jahreKinderKlassenstufe$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreKinderKlassenstufe).pipe(filterDefined);    
    selectedWettbewewerb$: Observable<WettbewerbDetailsGUIModel> = this.#store.select(fromDomain.selectedWettbewerb).pipe(filterDefined);
    selectedKlassenstufe$: Observable<KlassenstufeGUIModel> = this.#store.select(fromDomain.selectedKlassenstufe).pipe(filterDefined);

    loadWettbewerbe(): void {
        this.#store.dispatch(domainActions.lOAD_WETTBEWERBE());
    }

    loadWettbewerbsdetails(jahr: number): void {

        this.#loadWettbewerbsdetailsSubscription.unsubscribe();

        this.#loadWettbewerbsdetailsSubscription = this.#store.pipe(
            select(fromDomain.wettbewerbdetails),
            take(1)
        ).subscribe((wettbewerbe: WettbewerbDetailsGUIModel[]) => {
            // const wettbewerbLoaded = wettbewerbe.some(w => w.wettbewerb.jahr === jahr);
            const filtered = wettbewerbe.filter(g => '' + g.wettbewerb.jahr === '' + jahr);
            if (filtered.length === 1 && filtered[0].wettbewerb.beendet) {                
                this.#store.dispatch(domainActions.sELECT_WETTBEWERBDETAILS({wettbewerbGUIModel: filtered[0]}));
            } else {
                this.#store.dispatch(domainActions.lOAD_WETTBEWERB({jahr}));
            }
        });
    }

    getKlassenstufeDetails(jahr: number, pathParamKlassenstufe: string): void {
        let klassenstufe: Klassenstufe = 'IKID';

        switch (pathParamKlassenstufe) {
            case 'IKID': klassenstufe = 'IKID'; break;
            case 'EINS': klassenstufe = 'EINS'; break;
            case 'ZWEI': klassenstufe = 'ZWEI'; break;
        }

        this.#loadKlassenstufeDetails(jahr, klassenstufe);
    }

    #loadKlassenstufeDetails(jahr: number, klassenstufe: Klassenstufe): void {

        this.#loadKlassenstufeSubscription.unsubscribe();

        this.#loadKlassenstufeSubscription = this.#store.pipe(
            select(fromDomain.klassenstufeDetails),
            take(1)
        ).subscribe((klassenstufen: KlassenstufeGUIModel[]) => {

            const filtered = klassenstufen.filter(k => k.klassenstufeDetails.wettbewerbsjahr === '' + jahr && k.klassenstufeDetails.klassenstufe === klassenstufe);
            if (filtered.length === 1 && filtered[0].klassenstufeDetails.beendet) {
                this.#store.dispatch(domainActions.sELECT_KLASSENSTUFEDETAILS({klassenstufeGUIModel: filtered[0]}));
            } else {
                this.#store.dispatch(domainActions.lOAD_KLASSENSTUFE({jahr, klassenstufe}));
            }
            // this.#store.dispatch(domainActions.lOAD_KLASSENSTUFE({jahr, klassenstufe}));
        });
    }
}