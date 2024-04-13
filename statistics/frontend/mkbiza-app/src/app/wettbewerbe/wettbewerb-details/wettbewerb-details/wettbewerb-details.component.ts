import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Subscription, combineLatest } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { GenericPieChartComponent } from '../../generic-pie-chart/generic-pie-chart.component';
import { GenericBarChartComponent } from '../../generic-bar-chart/generic-bar-chart.component';
import { MatButtonModule } from '@angular/material/button';
import { ChartData } from 'chart.js';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { StatistikWettbewerbChartData, WettbewerbDetailsGUIModel } from '@mkbiza-app/domain-model';



@Component({
  selector: 'mkbiza-wettbewerb',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
    GenericBarChartComponent,
    GenericPieChartComponent,
    MatButtonModule
  ],
  templateUrl: './wettbewerb-details.component.html',
  styleUrl: './wettbewerb-details.component.scss',
})
export class WettbewerbDetailsComponent implements OnInit, OnDestroy {

  domainFacade = inject(DomainFacade);
  statistics!: StatistikWettbewerbChartData;

  chartDataKinderJeKlassenstufe!: ChartData<'pie', number[], string | string[]>;
  chartDataKinderJeTeilnahmeart!: ChartData<'pie', number[], string | string[]>;
  chartDataKinderJeSprache!: ChartData<'pie', number[], string | string[]>;

  #activatedRoute = inject(ActivatedRoute);
  #routeSubscription = new Subscription();
  #breakpointObserver = inject(BreakpointObserver);

  #wettbewerbSusbcription = new Subscription();


  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  ngOnInit(): void {
    this.#routeSubscription = this.#activatedRoute.params.subscribe(params => {
      const wettbewerbsjahr = params['id'];
      this.domainFacade.loadWettbewerbsdetails(wettbewerbsjahr);
    });

    this.#wettbewerbSusbcription = this.domainFacade.selectedWettbewewerb$.subscribe((wettbewerb) => {

      this.statistics = {
        chartDataSchulanmeldungenVersusSchulteilnahmen: { ...wettbewerb.chartData.chartDataSchulanmeldungenVersusSchulteilnahmen },
        chartModelKinderJeKlassenstufe: { ...wettbewerb.chartData.chartModelKinderJeKlassenstufe },
        chartDataKinderJeLand: { ...wettbewerb.chartData.chartDataKinderJeLand },
        chartModelKinderJeSprache: { ...wettbewerb.chartData.chartModelKinderJeSprache },
        chartModelKinderJeTeilnahmeart: { ...wettbewerb.chartData.chartModelKinderJeTeilnahmeart },
        chartDataMediane: { ...wettbewerb.chartData.chartDataMediane },
        chartDataSchulenJeLand: { ...wettbewerb.chartData.chartDataSchulenJeLand }
      };

      this.chartDataKinderJeKlassenstufe = {
        datasets: [
          {
            data: this.statistics.chartModelKinderJeKlassenstufe.data
          }
        ],
        labels: this.statistics.chartModelKinderJeKlassenstufe.labels
      }

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
    this.#routeSubscription.unsubscribe();
    this.#wettbewerbSusbcription.unsubscribe();
  }

}
