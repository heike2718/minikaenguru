import { createFeatureSelector, createSelector } from '@ngrx/store';
import * as fromDownload from './download.reducer';

export const downloadState = createFeatureSelector<fromDownload.DownloadState>(fromDownload.downloadFeatureKey);

export const downloadInProgress = createSelector(downloadState, s => s.downloadInProgress);

