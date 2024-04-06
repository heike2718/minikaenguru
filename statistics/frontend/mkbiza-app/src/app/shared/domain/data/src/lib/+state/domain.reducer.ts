import { createFeature, createReducer, on } from "@ngrx/store";
import { domainActions } from "./domain.actions";
import { WettbewerbOverview } from "@mkbiza-app/domain-model";

export interface DomainState {
    readonly wettbewerbe: WettbewerbOverview[];
};

const initialDomainState: DomainState = {
    wettbewerbe: []
};

export const domainFeature = createFeature({
    name: 'domain',
    reducer: createReducer(
        initialDomainState,
        on(domainActions.wETTBEWERBE_LOADED, (state,action): DomainState => {
            return {...state, wettbewerbe: action.wettbewerbe}
        })
    )
});

