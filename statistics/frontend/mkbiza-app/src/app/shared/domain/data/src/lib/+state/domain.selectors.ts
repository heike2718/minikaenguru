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

export const fromDomain = {
    wettbewerbe,
    jahreAnzahlKinder,
    jahreKinderKlassenstufe,
    jahreMediane
};
