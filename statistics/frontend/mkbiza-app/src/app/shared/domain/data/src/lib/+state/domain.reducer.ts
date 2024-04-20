import { createFeature, createReducer, on } from "@ngrx/store";
import { domainActions } from "./domain.actions";
import {
    StatistikJahreChartData,
    StatistikWettbewerbChartData,
    WettbewerbDetailsGUIModel,
    WettbewerbOverview,
    mapToAnmeldungenVersusTeilnahmen,
    mapToChartDataJahreAnzahlKinder,
    mapToChartDataJahreKinderKlassenstufe,
    mapToChartDataJahreMediane,
    mapToChartDataSingleDataset,
    mapToChartDataMediane,
    mapToChartModel,
    KlassenstufeGUIModel,
    KlassenstufeDetails,
    StatistikKlassenstufeChartData,
    mapToKlassenstufeMedianChartData,
    Aufgabendetails,
    AufgabeGUIModel
} from "@mkbiza-app/domain-model";

export interface DomainState {
    readonly wettbewerbe: WettbewerbOverview[];
    readonly statistikJahreChartData: StatistikJahreChartData | undefined;
    readonly wettbewerbdetails: WettbewerbDetailsGUIModel[];
    readonly selectedWettbewerb: WettbewerbDetailsGUIModel | undefined;
    readonly klassenstufendetails: KlassenstufeGUIModel[];
    readonly selectedKlassenstufe: KlassenstufeGUIModel | undefined;
};

const initialDomainState: DomainState = {
    wettbewerbe: [],
    statistikJahreChartData: undefined,
    wettbewerbdetails: [],
    selectedWettbewerb: undefined,
    klassenstufendetails: [],
    selectedKlassenstufe: undefined
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
                chartDataKinderJeLand: mapToChartDataSingleDataset(wettbewerb.kinderJeLand, 'Kinder'),
                chartModelKinderJeSprache: mapToChartModel(wettbewerb.kinderJeSprache),
                chartModelKinderJeTeilnahmeart: mapToChartModel(wettbewerb.kinderJeTeilnahmeart),
                chartDataMediane: mapToChartDataMediane(wettbewerb),
                chartDataSchulenJeLand: mapToChartDataSingleDataset(wettbewerb.schulenJeLand, 'Schulen')
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
        }),
        on(domainActions.kLASSENSTUFE_LOADED, (state, action): DomainState => {

            const klassenstufeDetails: KlassenstufeDetails = action.klassenstufeDetails;

            const chartDataKlassenstufe: StatistikKlassenstufeChartData = {
                chartDataKinderJeLand: mapToChartDataSingleDataset(klassenstufeDetails.kinderJeLand, 'Kinder'),
                chartDataKinderJePunktintervall: mapToChartDataSingleDataset(klassenstufeDetails.kinderJePunktintervall, 'Kinder je Punktintervall'),
                chartModelKinderJeSprache: mapToChartModel(klassenstufeDetails.kinderJeSprache),
                chartModelKinderJeTeilnahmeart: mapToChartModel(klassenstufeDetails.kinderJeTeilnahmeart),
                chartDataMedianUndGesamtpunkte: mapToKlassenstufeMedianChartData(klassenstufeDetails.medianUndGesamtpunkte)
            };

            const klassenstufeKey = klassenstufeDetails.wettbewerbsjahr + '-' + klassenstufeDetails.klassenstufe;

            const alreadyLoaded = state.klassenstufendetails.some(kd =>
                kd.klassenstufeDetails.wettbewerbsjahr + '-' + kd.klassenstufeDetails.klassenstufe === klassenstufeKey);

            // console.log('klassenstufendetails.length = ' + state.klassenstufendetails.length + ', already loaded: ' + alreadyLoaded);

            const aufgaben: Aufgabendetails[] = klassenstufeDetails.aufgaben;
            const aufgabenGUIModel: AufgabeGUIModel[] = [];

            for (let i = 0; i < aufgaben.length; i++) {

                const theAufgabendetails: Aufgabendetails = aufgaben[i];

                const anzahl = theAufgabendetails.anzahlenJeLoesungsbuchstabe.map(g => g.anzahl).reduce((sum, current) => sum + current, 0);

                const guiModel: AufgabeGUIModel = {
                    aufgabendetails: theAufgabendetails,
                    chartData: {
                        chartDataAnzahlenJeLoesungsbuchstabe: anzahl > 0 ? mapToChartDataSingleDataset(theAufgabendetails.anzahlenJeLoesungsbuchstabe, 'Anzahl Antworten je Lösungsbuchstabe') : undefined,
                        chartModelAnzahlenJeWertungscode: mapToChartModel(theAufgabendetails.anzahlenJeWertungscode)
                    }
                };

                aufgabenGUIModel.push(guiModel);
            }

            const klassenstufeGuiModel: KlassenstufeGUIModel = {
                klassenstufeDetails: klassenstufeDetails,
                aufgabenGUIModel: aufgabenGUIModel,
                chartDataKlassenstufe: chartDataKlassenstufe
            }

            return {
                ...state,
                klassenstufendetails: alreadyLoaded ? [...state.klassenstufendetails] : [...state.klassenstufendetails, klassenstufeGuiModel],
                selectedKlassenstufe: klassenstufeGuiModel
            }
        }),
        on(domainActions.sELECT_KLASSENSTUFEDETAILS, (state, action): DomainState => {

            return {
                ...state,
                selectedKlassenstufe: action.klassenstufeGUIModel
            }
        }),
    )
});

