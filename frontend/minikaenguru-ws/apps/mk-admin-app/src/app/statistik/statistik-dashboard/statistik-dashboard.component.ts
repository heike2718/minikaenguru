import { Component, OnDestroy, OnInit } from '@angular/core';
import { StatistikFacade } from '../statistik.facade';
import { StatistikEntity, StatistikGruppeninfo } from '../statistik.model';

@Component({
  selector: 'mka-statistik-dashboard',
  templateUrl: './statistik-dashboard.component.html',
  styleUrls: ['./statistik-dashboard.component.css']
})
export class StatistikDashboardComponent implements OnInit, OnDestroy {

  private panelMap: Map<string, StatistikEntity> = new Map();

  constructor(public statistikFacade: StatistikFacade) {

    this.panelMap.set('ngb-panel-0', 'KINDER');

  }

  ngOnInit(): void {
  }

  ngOnDestroy(): void {    
  }

  beforeChange($event: any): void {

    const entity: StatistikEntity | undefined = this.panelMap.get($event.panelId);
    const expanded: boolean = $event.nextState;

    if (entity) {

      if (expanded) {
        this.statistikFacade.expandStatistik(entity);
      } else {
        this.statistikFacade.collapseStatistik();
      }
    }
  }
}
