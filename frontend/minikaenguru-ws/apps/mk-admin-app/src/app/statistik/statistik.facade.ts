import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppState } from '../reducers';
import { StatistikService } from './statistic.service';
import * as StatistikSelectors from './+state/statistik.selectors';
import * as StatistikActions from './+state/statistic.actions';
import { Store } from '@ngrx/store';
import { GlobalErrorHandlerService } from '../infrastructure/global-error-handler.service';
import { StatistikEntity, StatistikGruppeninfo, StatistikGruppeninfoMap, StatistikGruppeninfoWithID } from './statistik.model';


@Injectable({
	providedIn: 'root'
})
export class StatistikFacade {

    public statistikenLoading$: Observable<boolean> = this.store.select(StatistikSelectors.statistikLoading);
    public statistiken$: Observable<StatistikGruppeninfo[]> = this.store.select(StatistikSelectors.statistiken);
    public expandedStatistik$: Observable<StatistikGruppeninfo | undefined> = this.store.select(StatistikSelectors.expandedStatistik);

    private statistikenMap: StatistikGruppeninfoWithID[] = [];


    constructor(private statistikService: StatistikService
        , private store: Store<AppState>
        , private errorService: GlobalErrorHandlerService) {


            this.store.select(StatistikSelectors.statistikMap).subscribe(
                m => this.statistikenMap = m
            );
        }

    public expandStatistik(entity: StatistikEntity): void {

        const map: StatistikGruppeninfoMap = new StatistikGruppeninfoMap(this.statistikenMap);

        if (map.has(entity)) {

           const statistik =  map.get(entity);
           this.store.dispatch(StatistikActions.expandStatistik({statistik: statistik}));
           return;

        }

        if (entity === 'KINDER') {

            this.statistikService.loadStatistikKinder().subscribe(

                statistik => {

                    this.store.dispatch(StatistikActions.statistikLoaded({statistik: statistik}));

                },
				(error => {
					this.store.dispatch(StatistikActions.loadFinishedWithError());
					this.errorService.handleError(error);
				})
            )

        }
    }

    public collapseStatistik(): void {

        this.store.dispatch(StatistikActions.collapsStatistik());
    }

    public resetModule(): void {

        this.store.dispatch(StatistikActions.resetStatistiken());
    }
}
