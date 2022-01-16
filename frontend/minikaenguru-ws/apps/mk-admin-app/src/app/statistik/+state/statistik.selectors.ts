import { createFeatureSelector, createSelector } from '@ngrx/store';
import { StatistikGruppeninfoMap } from '../statistik.model';
import * as fromStatistikReducer from './statistik.reducer';

const statistikState = createFeatureSelector<fromStatistikReducer.StatistikState>(fromStatistikReducer.statistikFeatureKey);

export const statistikLoading = createSelector(statistikState, s => s.loading);
export const statistikMap = createSelector(statistikState, s => s.statistiken);
export const statistiken = createSelector(statistikMap, m => new StatistikGruppeninfoMap(m).toArray());
export const statistikKinder = createSelector(statistikState, s => s.statistikKinder);
export const statistikLoesungszettel = createSelector(statistikState, s => s.statistikLoesungszettel);

