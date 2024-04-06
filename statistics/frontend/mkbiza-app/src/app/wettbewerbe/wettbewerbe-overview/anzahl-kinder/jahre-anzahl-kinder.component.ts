import { Component, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
// import DatalabelsPlugin from 'chartjs-plugin-datalabels';

// https://valor-software.com/ng2-charts/bar

@Component({
  selector: 'mkbiza-jahre-anzahl-kinder',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './jahre-anzahl-kinder.component.html',
  styleUrl: './jahre-anzahl-kinder.component.scss',
})
export class JahreAnzahlKinderComponent {

  @ViewChild(BaseChartDirective) chart: BaseChartDirective<'bar'> | undefined;

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    // We use these empty structures as placeholders for dynamic theming.
    scales: {
      x: {},
      y: {
        min: 10,
      },
    },
    plugins: {
      legend: {
        display: true,
      },
      // datalabels: {
      //   anchor: 'end',
      //   align: 'end',
      // },
    },
  };

  barChartType = 'bar' as const;

  barChartData: ChartData<'bar'> = {
    labels: ['2006', '2007', '2008', '2009', '2010', '2011', '2012'],
    datasets: [
      { data: [65, 59, 80, 81, 56, 55, 40], label: 'Series A' },
      { data: [28, 48, 40, 19, 86, 27, 90], label: 'Series B' },
    ],
  };
}