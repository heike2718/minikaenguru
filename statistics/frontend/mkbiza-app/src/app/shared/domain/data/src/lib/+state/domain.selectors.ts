import { createSelector } from "@ngrx/store";
import { domainFeature } from './domain.reducer';

const { selectDomainState } = domainFeature;

const wettbewerbe = createSelector(
    selectDomainState,
    (state) => state.wettbewerbe
);

const jahreAnzahlKinder = createSelector(
    selectDomainState,
    (state) => state.statistikJahreChartData?.chartDataJahreAnzahlKinder
);

const jahreMediane = createSelector(
    selectDomainState,
    (state) => state.statistikJahreChartData?.chartDataJahreMediane
);

const jahreKinderKlassenstufe = createSelector(
    selectDomainState,
    (state) => state.statistikJahreChartData?.chartDataJahreKinderKlassenstufe
);

const wettbewerbdetails = createSelector(
    selectDomainState,
    (state) => state.wettbewerbdetails
);

const selectedWettbewerb = createSelector(
    selectDomainState,
    (state) => state.selectedWettbewerb
);

const wettbewerbIDs = createSelector(
    selectDomainState,
    (state) => state.wettbewerbe.map(w => w.jahr)
);

export const fromDomain = {
    wettbewerbe,
    jahreAnzahlKinder,
    jahreKinderKlassenstufe,
    jahreMediane,
    wettbewerbdetails,
    selectedWettbewerb,
    wettbewerbIDs
};
