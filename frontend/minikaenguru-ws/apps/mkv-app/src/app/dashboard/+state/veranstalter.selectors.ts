import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromVeranstalter from './veranstalter.reducer';

export const selectVeranstalterState = createFeatureSelector<fromVeranstalter.VeranstalterState>(fromVeranstalter.veranstalterFeatureKey);

export const teilnahmenummern = createSelector(selectVeranstalterState, s => s.teilnahmenummern);
export const teilnahmenummernLoaded = createSelector(selectVeranstalterState, s => s.teilnahmenummernLoaded);


