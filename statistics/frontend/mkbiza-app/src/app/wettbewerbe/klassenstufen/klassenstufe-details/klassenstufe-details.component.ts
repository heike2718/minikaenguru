import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChartData } from 'chart.js';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Klassenstufe, StatistikKlassenstufeChartData } from '@mkbiza-app/domain-model';
import { GenericBarChartComponent } from '../../generic-bar-chart/generic-bar-chart.component';
import { GenericPieChartComponent } from '../../generic-pie-chart/generic-pie-chart.component';
import { AufgabeDetailsComponent } from '../aufgabe-details/aufgabe-details.component';

@Component({
  selector: 'mkbiza-klassenstufe',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
    GenericBarChartComponent,
    GenericPieChartComponent,
    AufgabeDetailsComponent
  ],
  templateUrl: './klassenstufe-details.component.html',
  styleUrl: './klassenstufe-details.component.scss',
})
export class KlassenstufeDetailsComponent implements OnInit, OnDestroy {

  domainFacade = inject(DomainFacade);
  klassenstufeLabel = '';
  statistics!: StatistikKlassenstufeChartData;

  chartDataKinderJeTeilnahmeart!: ChartData<'pie', number[], string | string[]>;
  chartDataKinderJeSprache!: ChartData<'pie', number[], string | string[]>;

  #breakpointObserver = inject(BreakpointObserver);
  #activatedRoute = inject(ActivatedRoute);
  #routeSubscription = new Subscription();
  #klassenstufeSubscription = new Subscription();

  #jahr!: number;

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  ngOnInit(): void {

    this.#routeSubscription = this.#activatedRoute.params.subscribe(params => {
      this.#jahr = params['id'];
      const pathparamKlassenstufe = params['klassenstufe'];
      let klassenstufe: Klassenstufe = 'IKID';

      switch (pathparamKlassenstufe) {
        case 'IKID': this.klassenstufeLabel = 'Inklusion'; klassenstufe = 'IKID'; break;
        case 'EINS': this.klassenstufeLabel = 'Klasse 1'; klassenstufe = 'EINS'; break;
        case 'ZWEI': this.klassenstufeLabel = 'Klasse 2'; klassenstufe = 'ZWEI'; break;
      }
      this.domainFacade.loadKlassenstufeDetails(this.#jahr, klassenstufe);
    });

    this.#klassenstufeSubscription = this.domainFacade.selectedKlassenstufe$.subscribe((klassenstufeGuiModel) => {

      // brauchen swallowCopy, weil die Teile readonly sind
      this.statistics = {
        chartDataKinderJePunktintervall: { ...klassenstufeGuiModel.chartDataKlassenstufe.chartDataKinderJePunktintervall },
        chartDataKinderJeLand: { ...klassenstufeGuiModel.chartDataKlassenstufe.chartDataKinderJeLand },
        chartModelKinderJeSprache: { ...klassenstufeGuiModel.chartDataKlassenstufe.chartModelKinderJeSprache },
        chartModelKinderJeTeilnahmeart: { ...klassenstufeGuiModel.chartDataKlassenstufe.chartModelKinderJeTeilnahmeart },
        chartDataMedianUndGesamtpunkte: { ...klassenstufeGuiModel.chartDataKlassenstufe.chartDataMedianUndGesamtpunkte }
      };

      this.chartDataKinderJeTeilnahmeart = {
        datasets: [
          {
            data: this.statistics.chartModelKinderJeTeilnahmeart.data
          }
        ],
        labels: this.statistics.chartModelKinderJeTeilnahmeart.labels
      }

      this.chartDataKinderJeSprache = {
        datasets: [
          {
            data: this.statistics.chartModelKinderJeSprache.data
          }
        ],
        labels: this.statistics.chartModelKinderJeSprache.labels
      }
    });
  }

  ngOnDestroy(): void {

    this.#breakpointObserver.ngOnDestroy();
    this.#routeSubscription.unsubscribe();
    this.#klassenstufeSubscription.unsubscribe();
  }

}
