import { createSelector } from "@ngrx/store";
import { domainFeature } from './domain.reducer';

const { selectDomainState } = domainFeature;

const wettbewerbe = createSelector(
    selectDomainState,
    (state) => state.wettbewerbe
);

export const fromDomain = {
    wettbewerbe
};
