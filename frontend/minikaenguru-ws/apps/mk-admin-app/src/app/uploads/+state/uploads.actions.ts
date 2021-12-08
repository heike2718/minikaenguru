import { createAction, props } from '@ngrx/store';
import { UploadMonitoringInfo } from '../uploads.model';

export const startLoading = createAction(
    '[UploadsFacade] diverse'
);

export const serviceCallFinishedWithError = createAction(
    '[UploadsFacade] load any'
);

export const sizeUploadInfosLoaded = createAction(
    '[UploadsFacade] countUploads',
    props<{size: number}>()
);

export const uploadInfosLoaded = createAction(
    '[UploadsFacade] getOrLoadUploadInfos',
    props<{uploadInfos: UploadMonitoringInfo[]}>()
);

export const uploadPageSelected = createAction(
    '[UploadsFacade] getOrLoadNextPage',
    props<{uploadInfos: UploadMonitoringInfo[]}>()
);

export const resetUploads = createAction(
    '[NavbarComponent] - uploads login/logout'
);