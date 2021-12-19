import { Action, createReducer, on } from '@ngrx/store';
import { UploadMonitoringInfo, UploadMonitoringInfoWithID, UploadMonitoringInfoMap, UploadsMonitoringPage, UploadsMonitoringPageMap } from '../uploads.model';
import * as UploadsActions from './uploads.actions';

export const uploadsFeatureKey = 'mk-admin-app-uploads';

export interface UploadsState  {
    readonly loading: boolean;
    readonly sizeLoaded: boolean;
    readonly anzahlUploads: number;
    readonly uploadsMap: UploadMonitoringInfoWithID[];
    readonly selectedUploadInfo?: UploadMonitoringInfo;
    readonly pages: UploadsMonitoringPage[];
    readonly pageContent: UploadMonitoringInfo[];
};

const initialUploadsState: UploadsState = {
    loading: false,
    sizeLoaded: false,
    anzahlUploads: 0,
    uploadsMap: [],
    selectedUploadInfo: undefined,
    pages: [],
    pageContent: []
};

const uploadsReducer = createReducer(initialUploadsState, 

    on(UploadsActions.loadSize, (state, _action) => {
        return {...state, loading: true}
    }),
  
    on(UploadsActions.startLoading, (state, _action) => {

        return {...state, loading: true};
    }),

    on(UploadsActions.serviceCallFinishedWithError, (state, _action) => {

        return {...state, loading: false};
    }),

    on(UploadsActions.sizeUploadInfosLoaded, (state, action) => {
        return {...state, loading: false, anzahlUploads: action.size, sizeLoaded: true};
    }),

    on (UploadsActions.uploadPageLoaded, (state, action) => {

        const newPage: UploadsMonitoringPage = {pageNumber: action.pageNumber, content: action.content};
        const newPages: UploadsMonitoringPage[] = new UploadsMonitoringPageMap(state.pages).merge(newPage);

        return {...state, loading: false, pages: newPages, pageContent: newPage.content};
    }),

    on(UploadsActions.uploadInfosLoaded, (state, action) => {        
        const uploadsMap: UploadMonitoringInfoWithID[] = new UploadMonitoringInfoMap(state.uploadsMap).add(action.uploadInfos);
        return {...state, loading: false, uploadsMap: uploadsMap};
    }),

    on(UploadsActions.resetUploads, (_state, _action) => {

        return initialUploadsState;
    })
);

export function reducer(state: UploadsState | undefined, action: Action) {
    return uploadsReducer(state, action);
}


