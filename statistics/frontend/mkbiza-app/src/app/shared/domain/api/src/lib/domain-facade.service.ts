import { Injectable, inject } from "@angular/core";
import { domainActions, fromDomain } from '@mkbiza-app/domain-data';

import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import { WettbewerbOverview } from "../../../model/src/lib/domain-model";

@Injectable({
    providedIn: 'root'
})
export class DomainFacade {

    #store = inject(Store);

    wettbewerbe$: Observable<WettbewerbOverview[]> = this.#store.select(fromDomain.wettbewerbe);

    loadWettbewerbe(): void {
        this.#store.dispatch(domainActions.lOAD_WETTBEWERBE());
    }

}