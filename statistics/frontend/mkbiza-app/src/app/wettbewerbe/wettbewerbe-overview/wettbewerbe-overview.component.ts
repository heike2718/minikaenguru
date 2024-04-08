import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Subscription, combineLatest } from 'rxjs';
import { ChartData } from "chart.js";
import { GenericBarChartComponent } from '../generic-bar-chart/generic-bar-chart.component';
@Component({
  selector: 'mkbiza-wettbewerbe',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
    GenericBarChartComponent
  ],
  templateUrl: './wettbewerbe-overview.component.html',
  styleUrl: './wettbewerbe-overview.component.scss',
})
export class WettbewerbeOverviewComponent implements OnInit, OnDestroy{

  domainFacade = inject(DomainFacade);
  chartDataJahreKinder!: ChartData<'bar'>;
  chartDataKinderKlassenstufen!: ChartData<'bar'>;
  chartDataMediane!: ChartData<'bar'>;


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
        
        // die Observables sind readonly. Beim rezize des Screens bekommt man einen Fehler, wenn man keine swallow-Kopie macht
        this.chartDataJahreKinder = {...chartDataJahreKinder};
        this.chartDataKinderKlassenstufen = {...chartDataKinderKlassenstufen};
        this.chartDataMediane = {...chartDataMediane};
      }
    });
  }

  ngOnDestroy(): void {
    this.#combinedDataSubscription.unsubscribe();
  }

}
