import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromAbout from './about.reducers';



export const selectAboutInfoState = createFeatureSelector<fromAbout.AboutInfoState>(fromAbout.aboutFeatureKey);

export const getApiVersion = createSelector(
	selectAboutInfoState,
	state => state.aboutInfos !== undefined ? state.aboutInfos.apiVersion : ''
);
