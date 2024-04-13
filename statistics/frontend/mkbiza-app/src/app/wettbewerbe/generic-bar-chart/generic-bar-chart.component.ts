import { Component, Input, OnDestroy, OnInit, ViewChild, booleanAttribute, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'mkbiza-bar-chart',
  standalone: true,
  imports: [CommonModule, BaseChartDirective, NgIf],
  templateUrl: './generic-bar-chart.component.html',
  styleUrl: './generic-bar-chart.component.scss',
})
export class GenericBarChartComponent implements OnInit {

  @ViewChild(BaseChartDirective) chart: BaseChartDirective<'bar'> | undefined;

  @Input()
  headline = '';

  @Input()
  chartData!: ChartData<'bar'>;

  @Input()
  myCanvasId: string = '';

  @Input({transform: booleanAttribute})
  showLegend!: boolean;

  chartOptions!: ChartConfiguration<'bar'>['options'];

  #breakpointObserver = inject(BreakpointObserver);

  ngOnInit(): void {

    this.chartOptions = {
      maintainAspectRatio: false,
      responsive: true,
      scales: {
        x: {},
        y: {
          min: 0,
        },
      },
      plugins: {
        legend: {
          display: this.showLegend,
        },
        // datalabels: {
        //   anchor: 'end',
        //   align: 'end',
        // }
      },
    }
  }

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

}
