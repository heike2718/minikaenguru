import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomainFacade } from '@mks/domain-api';
import { Subscription, combineLatest } from 'rxjs';
import { ChartData } from "chart.js";
import { GenericBarChartComponent } from '../generic-bar-chart/generic-bar-chart.component';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { GenericLineChartComponent } from '../generic-line-chart/generic-line-chart.component';

@Component({
    selector: 'mks-wettbewerbe',
    imports: [
        CommonModule,
        GenericBarChartComponent,
        GenericLineChartComponent,
        MatButtonModule
    ],
    templateUrl: './wettbewerbe-overview.component.html',
    styleUrl: './wettbewerbe-overview.component.scss'
})
export class WettbewerbeOverviewComponent implements OnInit, OnDestroy{

  domainFacade = inject(DomainFacade);
  chartDataJahreKinder!: ChartData<'bar'>;
  chartDataKinderKlassenstufen!: ChartData<'bar'>;
  chartDataMediane!: ChartData<'bar'>;
  chartDataWochenteilnahmen!: ChartData<'line'>;
  


  #router = inject(Router);
  #combinedDataSubscription = new Subscription();

  ngOnInit(): void {

    this.#combinedDataSubscription = combineLatest([
      this.domainFacade.wettbewerbe$,
      this.domainFacade.jahreAnzahlKinder$,
      this.domainFacade.jahreKinderKlassenstufe$,
      this.domainFacade.jahreMediane$,
      this.domainFacade.aggregierteWochenteilnahmen$])
    .subscribe(([
      wettbewerbe,
      chartDataJahreKinder,
      chartDataKinderKlassenstufen,
      chartDataMediane,
      chartDataWochenteilnahmen
    ]) => {
      if (wettbewerbe.length > 0) {  
        
        // die Observables sind readonly. Beim resize des Screens bekommt man einen Fehler, wenn man keine swallow-Kopie macht
        this.chartDataJahreKinder = {...chartDataJahreKinder};
        this.chartDataKinderKlassenstufen = {...chartDataKinderKlassenstufen};
        this.chartDataMediane = {...chartDataMediane};
        this.chartDataWochenteilnahmen = {...chartDataWochenteilnahmen}        
      }
    });
  }

  ngOnDestroy(): void {
    this.#combinedDataSubscription.unsubscribe();
  }

  #breakpointObserver = inject(BreakpointObserver);

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  onWettbewerbClick(id: number): void {
    this.#router.navigate(['/wettbewerbe', id]);
  }
}
