import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromFeedback from './feedback.reducer';

const feedbackState = createFeatureSelector<fromFeedback.FeedbackState>(fromFeedback.feedbackFeatureKey);

export const guiModel$ = createSelector(feedbackState, s => s.guiModel);
export const bewertungsbogenCreated$ = createSelector(feedbackState, s => s.bewertungsbogenCreated);

