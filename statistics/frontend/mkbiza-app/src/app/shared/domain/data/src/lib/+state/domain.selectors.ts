import { createSelector } from "@ngrx/store";
import { domainFeature } from './domain.reducer';

const { selectDomainState } = domainFeature;

const wettbewerbe = createSelector(
    selectDomainState,
    (state) => state.wettbewerbe
);

const jahreAnzahlKinder = createSelector(
    selectDomainState,
    (state) => state.chartDataJahreAnzahlKinder
);

export const fromDomain = {
    wettbewerbe,
    jahreAnzahlKinder
};
