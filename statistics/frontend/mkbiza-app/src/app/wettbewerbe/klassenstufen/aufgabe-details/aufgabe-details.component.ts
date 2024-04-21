import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { AufgabeGUIModel, Passung, StatistikAufgabeChartData } from '@mkbiza-app/domain-model';
import { CdkAccordionModule } from '@angular/cdk/accordion';
import { MatExpansionModule } from '@angular/material/expansion';
import { AufgabeImagesComponent } from '../aufgabe-images/aufgabe-images.component';
import { ChartData } from 'chart.js';
import { GenericBarChartComponent } from '../../generic-bar-chart/generic-bar-chart.component';
import { GenericPieChartComponent } from '../../generic-pie-chart/generic-pie-chart.component';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { MatBadgeModule } from '@angular/material/badge';

@Component({
  selector: 'mkbiza-aufgabe',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    CdkAccordionModule,
    MatExpansionModule,
    AufgabeImagesComponent,
    GenericBarChartComponent,
    GenericPieChartComponent,
    MatBadgeModule
  ],
  templateUrl: './aufgabe-details.component.html',
  styleUrl: './aufgabe-details.component.scss',
})
export class AufgabeDetailsComponent implements OnInit {

  statistics!: StatistikAufgabeChartData;

  chartWertungscodeId!: string;
  chartLoesungsbuchstabenId!: string;

  chartDataAnzahlenJeWertungscode!: ChartData<'pie', number[], string | string[]>;

  @Input()
  aufgabe!: AufgabeGUIModel;

  @Input()
  anzahlKinder!: number;

  #breakpointObserver = inject(BreakpointObserver);

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  ngOnInit(): void {

    this.chartWertungscodeId = 'wertungscode-' + this.aufgabe.aufgabendetails.nummer;
    this.chartLoesungsbuchstabenId = 'loesungsbuchstaben-' + this.aufgabe.aufgabendetails.nummer;    

    // brauchen swallowCopy, weil die Teile readonly sind
    this.statistics = {
      chartDataAnzahlenJeLoesungsbuchstabe: this.aufgabe.chartData.chartDataAnzahlenJeLoesungsbuchstabe ? { ...this.aufgabe.chartData.chartDataAnzahlenJeLoesungsbuchstabe } : undefined,
      chartModelAnzahlenJeWertungscode: { ...this.aufgabe.chartData.chartModelAnzahlenJeWertungscode }
    }

    this.chartDataAnzahlenJeWertungscode = {
      datasets: [
        {
          data: this.statistics.chartModelAnzahlenJeWertungscode.data
        }
      ],
      labels: this.statistics.chartModelAnzahlenJeWertungscode.labels
    }

  }


}
