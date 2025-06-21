import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomainFacade } from '@mks/domain-api';
import { Subscription } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { GenericPieChartComponent } from '../generic-pie-chart/generic-pie-chart.component';
import { GenericBarChartComponent } from '../generic-bar-chart/generic-bar-chart.component';
import { MatButtonModule } from '@angular/material/button';
import { ChartData } from 'chart.js';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Gruppierungsitem, isGruppierungsitemsEmpty, Klassenstufe, StatistikWettbewerbChartData, WettbewerbDetailsGUIModel } from '@mks/domain-model';



@Component({
  selector: 'mks-wettbewerb',
  imports: [
    CommonModule,
    GenericBarChartComponent,
    GenericPieChartComponent,
    MatButtonModule
  ],
  templateUrl: './wettbewerb-details.component.html',
  styleUrl: './wettbewerb-details.component.scss'
})
export class WettbewerbDetailsComponent implements OnInit, OnDestroy {

  domainFacade = inject(DomainFacade);
  statistics!: StatistikWettbewerbChartData;

  showWochenstatistik = true;

  chartDataKinderJeKlassenstufe!: ChartData<'pie', number[], string | string[]>;
  chartDataKinderJeTeilnahmeart!: ChartData<'pie', number[], string | string[]>;
  chartDataKinderJeSprache!: ChartData<'pie', number[], string | string[]>;

  klassenstufeLabels: string[] = [];

  #router = inject(Router);
  #activatedRoute = inject(ActivatedRoute);
  #routeSubscription = new Subscription();
  #breakpointObserver = inject(BreakpointObserver);
  #jahr!: number;

  #wettbewerbSusbcription = new Subscription();



  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  ngOnInit(): void {
    this.#routeSubscription = this.#activatedRoute.params.subscribe(params => {
      this.#jahr = params['id'];
    });

    this.#wettbewerbSusbcription = this.domainFacade.selectedWettbewewerb$.subscribe((wettbewerb: WettbewerbDetailsGUIModel) => {

      this.showWochenstatistik = !isGruppierungsitemsEmpty(wettbewerb.wettbewerb.anzahlLoesungszettelJeWoche);
      this.#updateButtonlabels(wettbewerb);

      this.statistics = {
        chartDataSchulanmeldungenVersusSchulteilnahmen: { ...wettbewerb.chartData.chartDataSchulanmeldungenVersusSchulteilnahmen },
        chartModelKinderJeKlassenstufe: { ...wettbewerb.chartData.chartModelKinderJeKlassenstufe },
        chartDataKinderJeLand: { ...wettbewerb.chartData.chartDataKinderJeLand },
        chartModelKinderJeSprache: { ...wettbewerb.chartData.chartModelKinderJeSprache },
        chartModelKinderJeTeilnahmeart: { ...wettbewerb.chartData.chartModelKinderJeTeilnahmeart },
        chartDataMediane: { ...wettbewerb.chartData.chartDataMediane },
        chartDataSchulenJeLand: { ...wettbewerb.chartData.chartDataSchulenJeLand },
        chartDataAnzahlLoesungszettelJeWoche: { ...wettbewerb.chartData.chartDataAnzahlLoesungszettelJeWoche }
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

  onKlassenstufeClick(klassenstufeLabel: string): void {

    let klassenstufe: string = 'IKID';
    if ('Klasse 1' === klassenstufeLabel) {
      klassenstufe = 'EINS';
    }
    if ('Klasse 2' === klassenstufeLabel) {
      klassenstufe = 'ZWEI';
    }
    if ('Inklusion' === klassenstufeLabel) {
      klassenstufe = 'IKID';
    }

    this.#router.navigate(['/klassenstufen', this.#jahr, klassenstufe]);
  }

  gotoStart(): void {
    this.#router.navigateByUrl('/');
  }

  #updateButtonlabels(wettbewerb: WettbewerbDetailsGUIModel): void {
    // leeren
    this.klassenstufeLabels = [];

    const klassenstufen: Klassenstufe[] = wettbewerb.wettbewerb.klassenstufen;

    for (let i = 0; i < klassenstufen.length; i++) {

      const klassenstufe: Klassenstufe = klassenstufen[i];

      switch (klassenstufe) {
        case 'EINS': this.klassenstufeLabels.push('Klasse 1'); break;
        case 'IKID': this.klassenstufeLabels.push('Inklusion'); break;
        case 'ZWEI': this.klassenstufeLabels.push('Klasse 2'); break;
      }
    }
  }
}
