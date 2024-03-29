import { createFeatureSelector, createSelector } from '@ngrx/store';
import { UploadMonitoringInfoMap } from '../uploads.model';
import * as fromUploads from './uploads.reducer';

const uploadsState = createFeatureSelector<fromUploads.UploadsState>(fromUploads.uploadsFeatureKey);

const uploadsMap = createSelector(uploadsState, s => s.uploadsMap);

export const sizeLoaded = createSelector(uploadsState, s => s.sizeLoaded);
export const uploadInfos = createSelector(uploadsMap, m => new UploadMonitoringInfoMap(m).toArray());
export const loading = createSelector(uploadsState, s => s.loading);
export const anzahlUploads = createSelector(uploadsState, s => s.anzahlUploads);
export const pages = createSelector(uploadsState, s => s.pages);
export const pageContent = createSelector(uploadsState, s => s.pageContent);

export const sizeLoadedAndAnzahl = createSelector(sizeLoaded, anzahlUploads, (loaded, anzahl) => combineSizeLoadedAndAnzahl(loaded, anzahl))

function combineSizeLoadedAndAnzahl(loaded: boolean, size: number): {sizeLoaded: boolean, size: number} {
    return {
        sizeLoaded: loaded,
        size: size
    };
}
