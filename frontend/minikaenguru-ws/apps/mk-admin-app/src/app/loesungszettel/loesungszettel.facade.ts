import { Injectable } from '@angular/core';
import { Store } from '@ngrx/store';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { AppState } from '../reducers';
import { LoesungszettelService } from './loesungszettel.service';
import * as LoesungszettelActions from './+state/loesungszettel.actions';
import * as LoesungszettelSelectors from './+state/loesungszettel.selectors';
import { Observable } from 'rxjs';
import { Loesungszettel, LoesungszettelPage, LoesungszettelPageMap } from './loesungszettel.model';

@Injectable({
	providedIn: 'root'
})
export class LoesungszettelFacade {

    private jahr: number | undefined;

    private pages: LoesungszettelPage[] = [];

    public pageContent$: Observable<Loesungszettel[]> = this.store.select(LoesungszettelSelectors.pageContent);
    public anzahl$: Observable<number> = this.store.select(LoesungszettelSelectors.anzahlLoesungszettel);

    constructor(private store: Store<AppState>
        , private loesungszettelService: LoesungszettelService
        , private errorHandler: GlobalErrorHandlerService){

            this.store.select(LoesungszettelSelectors.pages).subscribe(
                pages => this.pages = pages
            );
    }

    public selectJahr(jahr: number | undefined): void {

        this.jahr = jahr;

        if (jahr === undefined) {
            this.store.dispatch(LoesungszettelActions.resetLoesungszettel());
            return;
        }

        this.store.dispatch(LoesungszettelActions.jahrSelected({jahr: jahr}));     

        this.loesungszettelService.countLoesungszettel(jahr).subscribe(

            anzahl => {
                this.store.dispatch(LoesungszettelActions.anzahlLoesungszettelLoaded({size: anzahl}));
                this.getOrLoadPage(1, 5);
            },
            (error => {
                this.store.dispatch(LoesungszettelActions.backendCallFinishedWithError());
                this.errorHandler.handleError(error);
            })
        );
    }

    public getOrLoadPage(page: number, pageSize: number): void {

        if (this.jahr === undefined) {
            return;
        }

        const map: LoesungszettelPageMap = new LoesungszettelPageMap(this.pages);

        if (map.has(page)) {
            this.store.dispatch(LoesungszettelActions.loesungszettelLoaded({pageNumber: page, loesungszettel: map.getContent(page)}));            
        } else {
            this.loadPage(page, pageSize, this.jahr);
        }
        
    }

// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private loadPage(page: number, pageSize: number, jahr: number): void {
        
        const offset = (page-1) * pageSize;

        this.store.dispatch(LoesungszettelActions.startLoading());

        this.loesungszettelService.loadPage(jahr, pageSize, offset).subscribe(

            treffermenge => {
                this.store.dispatch(LoesungszettelActions.loesungszettelLoaded({pageNumber: page, loesungszettel: treffermenge}));
            },
            (error => {
                this.store.dispatch(LoesungszettelActions.backendCallFinishedWithError());
                this.errorHandler.handleError(error);
            })
        );
    }




}