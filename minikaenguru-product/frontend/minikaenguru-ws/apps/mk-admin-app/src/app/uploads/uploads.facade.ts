import { Injectable } from '@angular/core';
import { UploadsService } from './uploads.service';
import * as UploadsActions from './+state/uploads.actions';
import * as UploadsSelectors from './+state/uploads.selectors';
import { Store } from '@ngrx/store';
import { AppState } from '../reducers';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { Observable, of } from 'rxjs';
import { UploadMonitoringInfo, UploadMonitoringInfoMap, UploadMonitoringInfoWithID, UploadsMonitoringPage, UploadsMonitoringPageMap, UploadType } from './uploads.model';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { textChangeRangeIsUnchanged } from 'typescript';

@Injectable(
    {providedIn: 'root'}
)
export class UploadsFacade {

    public sizeLoadedAndAnzahl$: Observable<{sizeLoaded: boolean, size: number}> = this.store.select(UploadsSelectors.sizeLoadedAndAnzahl);
    public loading$: Observable<boolean> = this.store.select(UploadsSelectors.loading);
    public uploadInfos$: Observable<UploadMonitoringInfo[]> = this.store.select(UploadsSelectors.uploadInfos);
    public anzahlUploads$: Observable<number> = this.store.select(UploadsSelectors.anzahlUploads);
    public pageContent$: Observable<UploadMonitoringInfo[]> = this.store.select(UploadsSelectors.pageContent);


    
    private pages: UploadsMonitoringPage[] = [];

    private uploadsMap!: UploadMonitoringInfoMap;

    private loggingOut: boolean = false;

    private sizeLoaded = false;

    constructor(private store: Store<AppState>
        , private uploadsService: UploadsService
        , private authService: AuthService
        , private errorHandler: GlobalErrorHandlerService){


            this.uploadInfos$.subscribe(

                uploadInfos => {
                    const items: UploadMonitoringInfoWithID[] = [];
                    uploadInfos.forEach(uploadInfo => items.push({uuid: uploadInfo.uuid, uploadMonitoringInfo: uploadInfo}));
                    this.uploadsMap = new UploadMonitoringInfoMap(items);
                }
            );

            this.store.select(UploadsSelectors.pages).subscribe(
                pages => this.pages = pages
            );

            this.authService.onLoggingOut$.subscribe(
                loggingOut => this.loggingOut = loggingOut
            );

            this.store.select(UploadsSelectors.sizeLoaded).subscribe(
                loaded => this.sizeLoaded = loaded
            );

            this.countUploads();
    }    


    private countUploads(): void {

        if (this.loggingOut || this.sizeLoaded) {
			return;
		}

        this.store.dispatch(UploadsActions.startLoading());

        this.uploadsService.countUploads().subscribe(

            anzahl => {
                this.store.dispatch(UploadsActions.sizeUploadInfosLoaded({size: anzahl}))
                this.sizeLoaded = true;
            },
            (error => {
                this.sizeLoaded = true;
                this.store.dispatch(UploadsActions.serviceCallFinishedWithError());
                this.errorHandler.handleError(error);
            }));
    }

    public getOrLoadNextPage(page: number, pageSize: number) {

        if (this.loggingOut) {
            return;
        }

        if (!this.sizeLoaded) {

            this.uploadsService.countUploads().subscribe(

                anzahl => {
                    this.store.dispatch(UploadsActions.sizeUploadInfosLoaded({size: anzahl}))                    
                },
                (error => {
                    this.store.dispatch(UploadsActions.serviceCallFinishedWithError());
                    this.errorHandler.handleError(error);
                }));
        } else {

            const map: UploadsMonitoringPageMap = new UploadsMonitoringPageMap(this.pages);

            if (map.has(page)) {
                this.store.dispatch(UploadsActions.uploadPageLoaded({pageNumber: page, content: map.getContent(page)}));    
            } else {
                this.loadPage(page, pageSize);
            }

        }
    }

    public getOrLoadUploadInfos(uploadType: UploadType, teilnahmenummer: string): void {

        if (this.loggingOut) {
            return;
        }

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

    private loadPage(page: number, pageSize: number): void {

        const offset = (page-1) * pageSize;

        this.uploadsService.loadPage(pageSize, offset).subscribe(
            
            uploadInfos => {
                this.store.dispatch(UploadsActions.uploadPageLoaded({pageNumber: page, content: uploadInfos})); 
            },
            (error => {
                this.store.dispatch(UploadsActions.serviceCallFinishedWithError());
                this.errorHandler.handleError(error);
            }));
    }


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
