import { Component, Input, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { ChartConfiguration, ChartData } from 'chart.js';
import { BaseChartDirective } from 'ng2-charts';
import { BreakpointObserver, BreakpointState, Breakpoints } from '@angular/cdk/layout';

@Component({
  selector: 'mkbiza-pie-chart',
  standalone: true,
  imports: [CommonModule, BaseChartDirective, NgIf],
  templateUrl: './generic-pie-chart.component.html',
  styleUrl: './generic-pie-chart.component.scss',
})
export class GenericPieChartComponent implements OnInit, OnDestroy {

  @ViewChild(BaseChartDirective) chart: BaseChartDirective<'bar'> | undefined;

  @Input()
  myCanvasId: string = '';

  @Input()
  headline = '';

  @Input()
  chartData!: ChartData<'pie', number[], string | string[]>

  chartOptions!: ChartConfiguration['options'];

  #breakpointObserver = inject(BreakpointObserver);

  ngOnInit(): void {

    this.#breakpointObserver
      .observe([Breakpoints.Small, Breakpoints.HandsetPortrait])
      .subscribe((state: BreakpointState) => {
        if (state.matches) {
          this.chartOptions = {
            plugins: {
              legend: {
                display: false
              }
            },
          }
        } else {
          this.chartOptions = {
            plugins: {
              legend: {
                display: true,
                position: 'right',
              }
            },
          }
        }
      });
  }

  ngOnDestroy(): void {
      this.#breakpointObserver.ngOnDestroy();
  }

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }
}

