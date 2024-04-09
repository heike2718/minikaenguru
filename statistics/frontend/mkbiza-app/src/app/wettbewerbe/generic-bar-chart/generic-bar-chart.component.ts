import { Component, Input, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'mkbiza-bar-chart',
  standalone: true,
  imports: [CommonModule,BaseChartDirective,NgIf],
  templateUrl: './generic-bar-chart.component.html',
  styleUrl: './generic-bar-chart.component.scss',
})
export class GenericBarChartComponent {

  @ViewChild(BaseChartDirective) chart: BaseChartDirective<'bar'> | undefined;

  @Input()
  headline = '';

  @Input()
  barChartData!: ChartData<'bar'>;

  @Input()
  myCanvasId: string = '';

  barChartType = 'bar' as const;

  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    maintainAspectRatio: false,
    responsive: true,
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

  #breakpointObserver = inject(BreakpointObserver);

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

}
