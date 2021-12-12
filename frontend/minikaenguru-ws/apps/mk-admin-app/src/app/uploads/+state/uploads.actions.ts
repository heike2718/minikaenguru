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

export const uploadPageLoaded = createAction(
    '[UploadsFacade] getOrLoadNextPage',
    props<{pageNumber: number, content: UploadMonitoringInfo[]}>()
);

export const uploadInfosLoaded = createAction(
    '[UploadsFacade] getOrLoadUploadInfos',
    props<{uploadInfos: UploadMonitoringInfo[]}>()
);

export const resetUploads = createAction(
    '[NavbarComponent] - uploads login/logout'
);