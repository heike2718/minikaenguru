import { Component, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Subscription, combineLatest } from 'rxjs';
import { ChartConfiguration, ChartData } from 'chart.js';

@Component({
  selector: 'mkbiza-jahre-kinder-klassenstufe',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './jahre-kinder-klassenstufe.component.html',
  styleUrl: './jahre-kinder-klassenstufe.component.scss',
})
export class JahreKinderKlassenstufeComponent implements OnInit, OnDestroy {

  @ViewChild(BaseChartDirective) chart: BaseChartDirective<'bar'> | undefined;

  #domainFacade = inject(DomainFacade);
  #combinedDataSubscription = new Subscription();

  barChartOptions!: ChartConfiguration<'bar'>['options'];
  barChartType = 'bar' as const;
  barChartData!: ChartData<'bar'>;

  ngOnInit(): void {

    this.#combinedDataSubscription = combineLatest([this.#domainFacade.wettbewerbe$, this.#domainFacade.jahreKinderKlassenstufe$])
      .subscribe(([wettbewerbe, chartData]) => {
        if (wettbewerbe.length > 0) {

          this.barChartOptions = {
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

          this.barChartData = { ...chartData };
        }
      });
  }

  ngOnDestroy(): void {
    this.#combinedDataSubscription.unsubscribe();
  }
}


