import { Action, createReducer, on } from '@ngrx/store';
import { UploadMonitoringInfo, UploadMonitoringInfoWithID, UploadMonitoringInfoMap } from '../uploads.model';
import * as UploadsActions from './uploads.actions';

export const uploadsFeatureKey = 'mk-admin-app-uploads';

export interface UploadsState  {
    readonly loading: boolean;
    readonly anzahlUploads: number;
    readonly uploadsMap: UploadMonitoringInfoWithID[];
    readonly selectedPage: UploadMonitoringInfo[]
    readonly selectedUploadInfo?: UploadMonitoringInfo;
};

const initialUploadsState: UploadsState = {
    loading: false,
    anzahlUploads: 0,
    uploadsMap: [],
    selectedPage: [],
    selectedUploadInfo: undefined
};

const uploadsReducer = createReducer(initialUploadsState, 
  
    on(UploadsActions.startLoading, (state, _action) => {

        return {...state, loading: true};
    }),

    on(UploadsActions.serviceCallFinishedWithError, (state, _action) => {

        return {...state, loading: false};
    }),

    on(UploadsActions.sizeUploadInfosLoaded, (state, action) => {
        return {...state, loading: false, anzahlUploads: action.size};
    }),

    on(UploadsActions.uploadInfosLoaded, (state, action) => {        
        const uploadsMap: UploadMonitoringInfoWithID[] = new UploadMonitoringInfoMap(state.uploadsMap).add(action.uploadInfos);
        return {...state, loading: false, uploadsMap: uploadsMap};
    }),

    on (UploadsActions.uploadPageSelected, (state, action) => {

        const uploadsMap: UploadMonitoringInfoWithID[] = new UploadMonitoringInfoMap(state.uploadsMap).merge(action.uploadInfos);
        return {...state, loading: false, uploadsMap: uploadsMap, selectedPage: action.uploadInfos};
    }),

    on(UploadsActions.resetUploads, (_state, _action) => {

        return initialUploadsState;
    })
);

export function reducer(state: UploadsState | undefined, action: Action) {
    return uploadsReducer(state, action);
}


