import { createFeature, createReducer, on } from "@ngrx/store";
import { domainActions } from "./domain.actions";
import { StatistikJahreChartData,
    WettbewerbDetailsGUIModel,
    WettbewerbOverview,
    mapToChartDataJahreAnzahlKinder,
    mapToChartDataJahreKinderKlassenstufe,
    mapToChartDataJahreMediane
} from "@mkbiza-app/domain-model";

export interface DomainState {
    readonly wettbewerbe: WettbewerbOverview[];
    readonly statistikJahreChartData: StatistikJahreChartData | undefined;
    readonly wettbewerbdetails: WettbewerbDetailsGUIModel[];
    readonly selectedWettbewerb: WettbewerbDetailsGUIModel | undefined;
};

const initialDomainState: DomainState = {
    wettbewerbe: [],
    statistikJahreChartData: undefined,
    wettbewerbdetails: [],
    selectedWettbewerb: undefined  
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
        }),
        on(domainActions.wETTBEWERB_LOADED, (state, action): DomainState => {

            const wettbewerb = action.wettbewerb;            
            

            return {
                ...state
            }
        })
    )
});

