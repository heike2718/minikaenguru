import { Injectable, inject } from "@angular/core";
import { domainActions, fromDomain } from '@mkbiza-app/domain-data';

import { Store, select } from "@ngrx/store";
import { Observable, Subscription, take } from "rxjs";
import { WettbewerbDetailsGUIModel, WettbewerbOverview } from "@mkbiza-app/domain-model";
import { ChartData } from "chart.js";
import { filterDefined } from "./filter-defined";

@Injectable({
    providedIn: 'root'
})
export class DomainFacade {

    #store = inject(Store);

    #loadWettbewerbsdetailsSubscription = new Subscription();

    wettbewerbIDs$: Observable<number[]> = this.#store.select(fromDomain.wettbewerbIDs);
    wettbewerbe$: Observable<WettbewerbOverview[]> = this.#store.select(fromDomain.wettbewerbe);
    jahreAnzahlKinder$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreAnzahlKinder).pipe(filterDefined);
    jahreMediane$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreMediane).pipe(filterDefined);
    jahreKinderKlassenstufe$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreKinderKlassenstufe).pipe(filterDefined);    
    selectedWettbewewerb$: Observable<WettbewerbDetailsGUIModel> = this.#store.select(fromDomain.selectedWettbewerb).pipe(filterDefined);

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
            const filtered = wettbewerbe.filter(w => w.wettbewerb.jahr === jahr);
            if (filtered.length === 1 && filtered[0].wettbewerb.beendet) {                
                this.#store.dispatch(domainActions.sELECT_WETTBEWERBDETAILS({wettbewerbGUIModel: filtered[0]}));
            } else {
                this.#store.dispatch(domainActions.lOAD_WETTBEWERB({jahr}));
            }
        });
    }
}