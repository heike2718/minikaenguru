import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartData } from 'chart.js';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { StatistikKlassenstufeChartData } from '@mkbiza-app/domain-model';
import { GenericBarChartComponent } from '../../generic-bar-chart/generic-bar-chart.component';
import { GenericPieChartComponent } from '../../generic-pie-chart/generic-pie-chart.component';
import { AufgabeDetailsComponent } from '../aufgabe-details/aufgabe-details.component';
import { MatButtonModule } from '@angular/material/button';
import { CdkAccordionModule } from '@angular/cdk/accordion';
import { MatExpansionModule } from '@angular/material/expansion';

@Component({
    selector: 'mkbiza-klassenstufe',
    imports: [
        CommonModule,
        GenericBarChartComponent,
        GenericPieChartComponent,
        AufgabeDetailsComponent,
        MatButtonModule,
        CdkAccordionModule,
        MatExpansionModule
    ],
    templateUrl: './klassenstufe-details.component.html',
    styleUrl: './klassenstufe-details.component.scss'
})
export class KlassenstufeDetailsComponent implements OnInit, OnDestroy {

  domainFacade = inject(DomainFacade);
  klassenstufeLabel = '';
  statistics!: StatistikKlassenstufeChartData;

  chartDataKinderJeTeilnahmeart!: ChartData<'pie', number[], string | string[]>;
  chartDataKinderJeSprache!: ChartData<'pie', number[], string | string[]>;

  #breakpointObserver = inject(BreakpointObserver);
  #activatedRoute = inject(ActivatedRoute);
  #router = inject(Router);
  #routeSubscription = new Subscription();
  #klassenstufeSubscription = new Subscription();

  #jahr!: number;

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  ngOnInit(): void {

    this.#routeSubscription = this.#activatedRoute.params.subscribe(params => {
      this.#jahr = params['id'];

      switch (params['klassenstufe']) {
        case 'IKID': this.klassenstufeLabel = 'Inklusion'; break;
        case 'EINS': this.klassenstufeLabel = 'Klasse 1'; break;
        case 'ZWEI': this.klassenstufeLabel = 'Klasse 2'; break;
      }
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

  gotoWettbewerb(): void {
    this.#router.navigate(['/wettbewerbe', this.#jahr]);
  }
}
