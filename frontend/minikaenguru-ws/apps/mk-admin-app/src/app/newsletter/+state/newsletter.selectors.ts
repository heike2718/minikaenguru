import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromNewsletters from './newsletter.reducer';
import { NewsletterMap } from '../newsletter.model';

const newsletterState = createFeatureSelector<fromNewsletters.NewslettersState>(fromNewsletters.newsletterFeatureKey);

const newsletterMap = createSelector(newsletterState, s => s.newsletterMap);

export const newsletters = createSelector(newsletterMap, m => new NewsletterMap(m).toArray());
export const selectedNewsletter = createSelector(newsletterState, s => s.selectedNewsletter);
export const newslettersLoaded = createSelector(newsletterState, s => s.newslettersLoaded);
export const loading = createSelector(newsletterState, s => s.loading);
export const newsletterEditorModel = createSelector(newsletterState, s => s.newsletterEditorModel);
