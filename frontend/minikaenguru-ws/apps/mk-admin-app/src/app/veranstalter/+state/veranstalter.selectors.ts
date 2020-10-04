import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVeranstalter from './veranstalter.reducer';
import { VeranstalterMap } from '../veranstalter.model';

export const veranstalterSate = createFeatureSelector<fromVeranstalter.VeranstalterState>(fromVeranstalter.veranstalterFeatureKey);

export const veranstalterMap = createSelector(veranstalterSate, s => s.veranstalterMap);
export const veranstalter = createSelector(veranstalterSate, s => new VeranstalterMap(s.veranstalterMap).toArray());
export const sucheFinished = createSelector(veranstalterSate, s => s.sucheFinished);
export const veranstalterLoading = createSelector(veranstalterSate, s => s.loading);

export const selectedVeranstalter = createSelector(veranstalterSate, s => s.selectedVeranstalter);


