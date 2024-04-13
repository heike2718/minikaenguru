import { createFeature, createReducer, on } from "@ngrx/store";
import { domainActions } from "./domain.actions";
import { StatistikJahreChartData,
    StatistikWettbewerbChartData,
    WettbewerbDetailsGUIModel,
    WettbewerbOverview,
    mapToAnmeldungenVersusTeilnahmen,
    mapToChartDataJahreAnzahlKinder,
    mapToChartDataJahreKinderKlassenstufe,
    mapToChartDataJahreMediane,
    mapToChartDataKinderLaender,
    mapToChartDataLaender,
    mapToChartDataMediane,
    mapToChartModel
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

            const chartData: StatistikWettbewerbChartData = {
                chartDataSchulanmeldungenVersusSchulteilnahmen: mapToAnmeldungenVersusTeilnahmen(wettbewerb),
                chartModelKinderJeKlassenstufe: mapToChartModel(wettbewerb.kinderJeKlassenstufe),
                chartDataKinderJeLand: mapToChartDataKinderLaender(wettbewerb, 'Kinder'),
                chartModelKinderJeSprache: mapToChartModel(wettbewerb.kinderJeSprache),
                chartModelKinderJeTeilnahmeart: mapToChartModel(wettbewerb.kinderJeTeilnahmeart),
                chartDataMediane: mapToChartDataMediane(wettbewerb),
                chartDataSchulenJeLand: mapToChartDataLaender(wettbewerb.schulenJeLand, 'Schulen')                
            };

            const alreadyLoaded = state.wettbewerbdetails.some(w => w.wettbewerb.jahr === wettbewerb.jahr);

            const wettbewerbGuiModel: WettbewerbDetailsGUIModel = {
                wettbewerb: wettbewerb,
                chartData: chartData
            };     
            

            return {
                ...state,
                wettbewerbdetails: alreadyLoaded ? [...state.wettbewerbdetails] : [...state.wettbewerbdetails, wettbewerbGuiModel],
                selectedWettbewerb: wettbewerbGuiModel
            }
        }),
        on(domainActions.sELECT_WETTBEWERBDETAILS, (state, action): DomainState => {

            return {
                ...state,
                selectedWettbewerb: action.wettbewerbGUIModel
            }
        })
    )
});

