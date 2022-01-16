import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { StatistikFacade } from '../statistik.facade';
import { StatistikEntity, StatistikGruppeninfo } from '../statistik.model';

@Component({
  selector: 'mka-statistik-dashboard',
  templateUrl: './statistik-dashboard.component.html',
  styleUrls: ['./statistik-dashboard.component.css']
})
export class StatistikDashboardComponent implements OnInit, OnDestroy {

  constructor(public statistikFacade: StatistikFacade) {}

  private expandedGruppeninfoSubscription: Subscription = new Subscription();

  ngOnInit(): void { }

  ngOnDestroy(): void {   
    this.expandedGruppeninfoSubscription.unsubscribe(); 
  }

  reloadContent(): void {

    this.statistikFacade.forceReloadAll();

  }

  beforeChange($event: any): void {

    const entity = $event.panelId as StatistikEntity;
    const expanded: boolean = $event.nextState;

    if (entity) {

      if (expanded) {
        this.statistikFacade.expandStatistik(entity);
      } else {
        this.statistikFacade.collapseStatistik(entity);
      }
    }
  }
}
