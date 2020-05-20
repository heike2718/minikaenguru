import { createFeatureSelector } from '@ngrx/store';
import * as fromVeranstalter from './veranstalter.reducer';

export const selectVeranstalterState = createFeatureSelector<fromVeranstalter.VeranstalterState>(fromVeranstalter.veranstalterFeatureKey);

