import { Injectable, inject } from "@angular/core";
import { domainActions, fromDomain } from '@mkbiza-app/domain-data';

import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import { WettbewerbOverview } from "@mkbiza-app/domain-model";
import { ChartData } from "chart.js";
import { filterDefined } from "./filter-defined";

@Injectable({
    providedIn: 'root'
})
export class DomainFacade {

    #store = inject(Store);

    wettbewerbe$: Observable<WettbewerbOverview[]> = this.#store.select(fromDomain.wettbewerbe);
    jahreAnzahlKinder$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreAnzahlKinder).pipe(filterDefined);
    jahreMediane$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreMediane).pipe(filterDefined);
    jahreKinderKlassenstufe$: Observable<ChartData<'bar'>> = this.#store.select(fromDomain.jahreKinderKlassenstufe).pipe(filterDefined);

    loadWettbewerbe(): void {
        this.#store.dispatch(domainActions.lOAD_WETTBEWERBE());
    }

}