import { Injectable } from '@angular/core';
import { UploadsService } from './uploads.service';
import * as UploadsActions from './+state/uploads.actions';
import * as UploadsSelectors from './+state/uploads.selectors';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Observable, of } from 'rxjs';
import { UploadMonitoringInfo, UploadMonitoringInfoMap, UploadMonitoringInfoWithID, UploadType } from './uploads.model';

@Injectable(
    {providedIn: 'root'}
)
export class UploadsFacade {

    public loading$: Observable<boolean> = this.store.select(UploadsSelectors.loading);
    public uploadInfos$: Observable<UploadMonitoringInfo[]> = this.store.select(UploadsSelectors.uploadInfos);
    public anzahlUploads$: Observable<number> = this.store.select(UploadsSelectors.anzahlUploads);
    public selectedPage$: Observable<UploadMonitoringInfo[]> = this.store.select(UploadsSelectors.selectedPage);

    private uploadsMap!: UploadMonitoringInfoMap;

    constructor(private store: Store<AppState>
        , private uploadsService: UploadsService
        , private errorHandler: GlobalErrorHandlerService){


            this.uploadInfos$.subscribe(

                uploadInfos => {
                    const items: UploadMonitoringInfoWithID[] = [];
                    uploadInfos.forEach(uploadInfo => items.push({uuid: uploadInfo.uuid, uploadMonitoringInfo: uploadInfo}));
                    this.uploadsMap = new UploadMonitoringInfoMap(items);
                }
            );
    }

    public countUploads(): void {

        this.store.dispatch(UploadsActions.startLoading());

        this.uploadsService.countUploads().subscribe(


            anzahl => {

                this.store.dispatch(UploadsActions.sizeUploadInfosLoaded({size: anzahl}));
            },
            (error => {
                this.store.dispatch(UploadsActions.serviceCallFinishedWithError());
                this.errorHandler.handleError(error);
            })
        );
    }

    public getOrLoadNextPage(page: number, pageSize: number) {

        const uploadInfos: UploadMonitoringInfo[] = this.uploadsMap.toArray();
        const offset = (page-1) * pageSize;

        if (uploadInfos.length >= (page-1) * pageSize + pageSize) {

            const uploadsInfoPage: UploadMonitoringInfo[] = uploadInfos.slice(offset, (page-1) * pageSize + pageSize);
            this.store.dispatch(UploadsActions.uploadPageSelected({uploadInfos: uploadsInfoPage}));
        }

       this.uploadsService.loadPage(pageSize, offset).subscribe(
            
        uploadInfos => {
            this.store.dispatch(UploadsActions.uploadPageSelected({uploadInfos: uploadInfos}));
        },
        (error => {
            this.store.dispatch(UploadsActions.serviceCallFinishedWithError());
            this.errorHandler.handleError(error);
        }));


    }

    public getOrLoadUploadInfos(uploadType: UploadType, teilnahmenummer: string): void {

        const uploadInfos: UploadMonitoringInfo[] = this.uploadsMap.findUploadInfoByTeilnahmeNummer(uploadType, teilnahmenummer);

        if (uploadInfos.length === 0) {


            switch(uploadType) {
                case 'KLASSENLISTE': this.loadUploadsKlassenlistenByTeilnahmenummer(teilnahmenummer); break;
                case 'AUSWERTUNG': break;
                default: break;
            }
        }
    }

    public trefferlisteLeeren(): void {

        this.store.dispatch(UploadsActions.resetUploads());
    }

// ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private loadUploadsKlassenlistenByTeilnahmenummer(teilnahmenummer: string): void {

        this.store.dispatch(UploadsActions.startLoading());

        this.uploadsService.loadUploadsKlassenlistenByTeilnahmenummer(teilnahmenummer).subscribe(
            
            uploadInfos => {

                this.store.dispatch(UploadsActions.uploadInfosLoaded({uploadInfos: uploadInfos}));

            },
            (error => {
                this.store.dispatch(UploadsActions.serviceCallFinishedWithError());
                this.errorHandler.handleError(error);
            }));


    }
    
}