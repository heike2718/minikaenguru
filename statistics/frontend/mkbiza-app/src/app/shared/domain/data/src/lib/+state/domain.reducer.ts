import { createFeature, createReducer, on } from "@ngrx/store";
import { domainActions } from "./domain.actions";
import { WettbewerbOverview, mapToChartDataJahrAnzahlKinder } from "@mkbiza-app/domain-model";
import { ChartData } from 'chart.js';

export interface DomainState {
    readonly wettbewerbe: WettbewerbOverview[];
    readonly chartDataJahreAnzahlKinder: ChartData<'bar'> | undefined;
};

const initialDomainState: DomainState = {
    wettbewerbe: [],
    chartDataJahreAnzahlKinder: undefined
};

export const domainFeature = createFeature({
    name: 'domain',
    reducer: createReducer(
        initialDomainState,
        on(domainActions.wETTBEWERBE_LOADED, (state, action): DomainState => {

            const wettbewerbe = action.wettbewerbe;
            const chartData = mapToChartDataJahrAnzahlKinder(wettbewerbe);

            return { ...state, wettbewerbe: action.wettbewerbe, chartDataJahreAnzahlKinder: chartData }
        })
    )
});

