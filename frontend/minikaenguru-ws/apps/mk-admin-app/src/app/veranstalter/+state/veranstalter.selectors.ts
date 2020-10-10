import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVeranstalter from './veranstalter.reducer';
import { VeranstalterMap } from '../veranstalter.model';

export const veranstalterState = createFeatureSelector<fromVeranstalter.VeranstalterState>(fromVeranstalter.veranstalterFeatureKey);

export const veranstalterMap = createSelector(veranstalterState, s => s.veranstalterMap);
export const veranstalter = createSelector(veranstalterState, s => new VeranstalterMap(s.veranstalterMap).toArray());
export const sucheFinished = createSelector(veranstalterState, s => s.sucheFinished);
export const veranstalterLoading = createSelector(veranstalterState, s => s.loading);

export const selectedVeranstalter = createSelector(veranstalterState, s => s.selectedVeranstalter);



