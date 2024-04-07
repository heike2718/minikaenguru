import { Component, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Subscription, combineLatest } from 'rxjs';
// import DatalabelsPlugin from 'chartjs-plugin-datalabels';

// https://valor-software.com/ng2-charts/bar

@Component({
  selector: 'mkbiza-jahre-anzahl-kinder',
  standalone: true,
  imports: [CommonModule, BaseChartDirective, NgIf],
  templateUrl: './jahre-anzahl-kinder.component.html',
  styleUrl: './jahre-anzahl-kinder.component.scss',
})
export class JahreAnzahlKinderComponent implements OnInit, OnDestroy {

  @ViewChild(BaseChartDirective) chart: BaseChartDirective<'bar'> | undefined;

  #domainFacade = inject(DomainFacade);
  #combinedDataSubscription = new Subscription();

  barChartOptions!: ChartConfiguration<'bar'>['options'];
  barChartType = 'bar' as const;
  barChartData!: ChartData<'bar'>;

  ngOnInit(): void {

    this.#combinedDataSubscription = combineLatest([this.#domainFacade.wettbewerbe$, this.#domainFacade.jahreAnzahlKinder$])
    .subscribe(([wettbewerbe, chartData]) => {
      if (wettbewerbe.length > 0) {
        
        this.barChartOptions  = {
          // We use these empty structures as placeholders for dynamic theming.
          scales: {
            x: {},
            y: {
              min: 0,
            },
          },
          plugins: {
            legend: {
              display: true,
            },
            // datalabels are not supported any more :(
            // datalabels: {
            //   anchor: 'end',
            //   align: 'end',
            // },
          },
        };
        
        this.barChartData = {...chartData};
      }
    });
  }

  ngOnDestroy(): void {
    this.#combinedDataSubscription.unsubscribe();
  }
}