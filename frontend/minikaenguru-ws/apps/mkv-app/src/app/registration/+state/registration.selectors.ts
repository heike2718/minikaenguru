import { createSelector, createFeatureSelector } from '@ngrx/store';

import * as fromRegistration from './registration.reducer';

export const selectRegistrationState = createFeatureSelector<fromRegistration.RegistrationState>(
	fromRegistration.registrationFeatureKey
);

export const selectRegistrationMode = createSelector(selectRegistrationState, s => s.mode);
export const selectNewsletterAboState = createSelector(selectRegistrationState, s => s.newsletterAbonieren);
export const selectSubmitStatus = createSelector(selectRegistrationState, s => s.submitEnabled);
export const selectShowSchulkatalog = createSelector(selectRegistrationState, s => s.showSchulkatalog);
export const selectShowSuccessDialog = createSelector(selectRegistrationState, s => s.showRegistrationSuccessDialog);
export const selectSuccessDialogContent = createSelector(selectRegistrationState, s => s.registrationSuccessMessage);
