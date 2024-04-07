import { createFeature, createReducer, on } from "@ngrx/store";
import { domainActions } from "./domain.actions";
import { StatistikJahreChartData, WettbewerbOverview, mapToChartDataJahreAnzahlKinder, mapToChartDataJahreKinderKlassenstufe, mapToChartDataJahreMediane } from "@mkbiza-app/domain-model";

export interface DomainState {
    readonly wettbewerbe: WettbewerbOverview[];
    readonly statistikJahreChartData: StatistikJahreChartData | undefined;
};

const initialDomainState: DomainState = {
    wettbewerbe: [],
    statistikJahreChartData: undefined    
};

export const domainFeature = createFeature({
    name: 'domain',
    reducer: createReducer(
        initialDomainState,
        on(domainActions.wETTBEWERBE_LOADED, (state, action): DomainState => {

            const wettbewerbe = action.wettbewerbe;            
            const statistikJahreChartData: StatistikJahreChartData = {
                chartDataJahreAnzahlKinder: mapToChartDataJahreAnzahlKinder(wettbewerbe),
                chartDataJahreKinderKlassenstufe: mapToChartDataJahreKinderKlassenstufe(wettbewerbe),
                chartDataJahreMediane: mapToChartDataJahreMediane(wettbewerbe)
            }

            return {
                ...state,
                wettbewerbe: action.wettbewerbe,
                statistikJahreChartData: statistikJahreChartData
            }
        })
    )
});

