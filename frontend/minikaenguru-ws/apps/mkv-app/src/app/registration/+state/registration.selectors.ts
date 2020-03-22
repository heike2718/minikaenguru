import { createSelector, createFeatureSelector } from '@ngrx/store';

import * as fromRegistration from './registration.reducer';

export const selectRegistrationState = createFeatureSelector<fromRegistration.RegistrationState>(
	fromRegistration.registrationFeatureKey
);

export const selectRegistrationMode = createSelector(selectRegistrationState, s => s.mode);
export const selectSubmitStatus = createSelector(selectRegistrationState, s => s.submitEnabled);
export const selectShowShulkatalog = createSelector(selectRegistrationState, s => s.showSchulkatalog);
