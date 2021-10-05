import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromMessages from './messages.reducer';

export const selectMessagesState =
	createFeatureSelector<fromMessages.MessagesState>(fromMessages.messagesFeatureKey);

export const message = createSelector(selectMessagesState, s => s.message);
