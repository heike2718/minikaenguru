import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromSchulen from './schulen.reducer';

export const schulenState = createFeatureSelector<fromSchulen.SchulenState>(fromSchulen.schulenFeatureKey);

export const allSchulen = createSelector(schulenState, s => s.schulen);

export const selectedSchule = createSelector(schulenState, s => s.selectedSchule);

export const schuleDetails = createSelector(selectedSchule, s => s !== undefined ? s.details : undefined);

export const schulenLoaded = createSelector(schulenState, s => s.schulenLoaded);

export const loading = createSelector(schulenState, s => s.loading);

