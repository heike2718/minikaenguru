import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomainFacade } from '@mks/domain-api';
import { Subscription, combineLatest } from 'rxjs';
import { ChartData } from "chart.js";
import { GenericBarChartComponent } from '../generic-bar-chart/generic-bar-chart.component';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';

@Component({
    selector: 'mks-wettbewerbe',
    imports: [
        CommonModule,
        GenericBarChartComponent,
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


  #router = inject(Router);
  #combinedDataSubscription = new Subscription();

  ngOnInit(): void {

    this.#combinedDataSubscription = combineLatest([
      this.domainFacade.wettbewerbe$,
      this.domainFacade.jahreAnzahlKinder$,
      this.domainFacade.jahreKinderKlassenstufe$,
      this.domainFacade.jahreMediane$])
    .subscribe(([
      wettbewerbe,
      chartDataJahreKinder,
      chartDataKinderKlassenstufen,
      chartDataMediane
    ]) => {
      if (wettbewerbe.length > 0) {  
        
        // die Observables sind readonly. Beim resize des Screens bekommt man einen Fehler, wenn man keine swallow-Kopie macht
        this.chartDataJahreKinder = {...chartDataJahreKinder};
        this.chartDataKinderKlassenstufen = {...chartDataKinderKlassenstufen};
        this.chartDataMediane = {...chartDataMediane};
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
