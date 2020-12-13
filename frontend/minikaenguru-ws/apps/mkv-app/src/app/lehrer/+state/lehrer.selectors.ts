import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromLehrer from './lehrer.reducer';

export const lehrerState = createFeatureSelector<fromLehrer.LehrerState>(fromLehrer.lehrerFeatureKey);

export const alleSchulen = createSelector(lehrerState, s => s.schulen);

export const selectedSchule = createSelector(lehrerState, s => s.selectedSchule);

export const schuleDetails = createSelector(selectedSchule, s => s !== undefined ? s.details : undefined);

export const showSchulkatalog = createSelector(lehrerState, s => s.showSchulkatalog);

export const schulenLoaded = createSelector(lehrerState, s => s.schulenLoaded);

export const loading = createSelector(lehrerState, s => s.loading);

export const lehrer = createSelector(lehrerState, s => s.lehrer);

export const showTextSchuleBereitsZugeordnet = createSelector(lehrerState, s => s.showTextSchuleBereitsZugeordnet);

export const btnAddMeToSchuleDisabled = createSelector(lehrerState, s => s.btnAddMeToSchuleDisabled);


