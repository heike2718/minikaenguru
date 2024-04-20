import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { ChartData } from 'chart.js';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Klassenstufe, StatistikKlassenstufeChartData, WettbewerbDetails, WettbewerbDetailsGUIModel } from '@mkbiza-app/domain-model';
import { GenericBarChartComponent } from '../../generic-bar-chart/generic-bar-chart.component';
import { GenericPieChartComponent } from '../../generic-pie-chart/generic-pie-chart.component';
import { AufgabeDetailsComponent } from '../aufgabe-details/aufgabe-details.component';
import { MatButtonModule } from '@angular/material/button';
import { CdkAccordionModule } from '@angular/cdk/accordion';
import { MatExpansionModule } from '@angular/material/expansion';
import { RohpunktitemComponent } from '../rohpunktitem/rohpunktitem.component';

@Component({
  selector: 'mkbiza-klassenstufe',
  standalone: true,
  imports: [
    CommonModule,
    NgIf,
    NgFor,
    GenericBarChartComponent,
    GenericPieChartComponent,
    AufgabeDetailsComponent,
    RohpunktitemComponent,
    MatButtonModule,
    CdkAccordionModule,
    MatExpansionModule
  ],
  templateUrl: './klassenstufe-details.component.html',
  styleUrl: './klassenstufe-details.component.scss',
})
export class KlassenstufeDetailsComponent implements OnInit, OnDestroy {

  domainFacade = inject(DomainFacade);
  klassenstufeLabel = '';
  statistics!: StatistikKlassenstufeChartData;

  klassenstufeLabels: string[] = [];

  chartDataKinderJeTeilnahmeart!: ChartData<'pie', number[], string | string[]>;
  chartDataKinderJeSprache!: ChartData<'pie', number[], string | string[]>;

  #breakpointObserver = inject(BreakpointObserver);
  #activatedRoute = inject(ActivatedRoute);
  #router = inject(Router);
  #routeSubscription = new Subscription();
  #wettbewerbSubscription = new Subscription();
  #klassenstufeSubscription = new Subscription();

  #jahr!: number;
  #klassenstufe!: Klassenstufe;
  #selectedWettbewerb: WettbewerbDetailsGUIModel | undefined;

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  ngOnInit(): void {

    this.#routeSubscription = this.#activatedRoute.params.subscribe(params => {
      this.#jahr = params['id'];
      this.#klassenstufe = 'IKID';

      switch (params['klassenstufe']) {
        case 'IKID': this.klassenstufeLabel = 'Inklusion'; this.#klassenstufe = 'IKID'; break;
        case 'EINS': this.klassenstufeLabel = 'Klasse 1'; this.#klassenstufe = 'EINS'; break;
        case 'ZWEI': this.klassenstufeLabel = 'Klasse 2'; this.#klassenstufe = 'ZWEI'; break;
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

      if (this.#selectedWettbewerb) {
        this.#updateButtonlabels(this.#selectedWettbewerb);
      }
    });

    this.#wettbewerbSubscription = this.domainFacade.selectedWettbewewerb$.subscribe((wettbewerb) => {
      if (wettbewerb) {
        this.#selectedWettbewerb = wettbewerb;
        this.#updateButtonlabels(this.#selectedWettbewerb);
      }
    });
  }

  ngOnDestroy(): void {

    this.#breakpointObserver.ngOnDestroy();
    this.#routeSubscription.unsubscribe();
    this.#klassenstufeSubscription.unsubscribe();
    this.#wettbewerbSubscription.unsubscribe();
  }

  gotoWettbewerb(): void {
    this.#router.navigate(['/wettbewerbe', this.#jahr]);
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

  #updateButtonlabels(wettbewerb: WettbewerbDetailsGUIModel): void {
    // leeren
    this.klassenstufeLabels = [];

    const klassenstufen: Klassenstufe[] = wettbewerb.wettbewerb.klassenstufen;

    for (let i = 0; i < klassenstufen.length; i++) {

      const klassenstufe: Klassenstufe = klassenstufen[i];

      if (this.#klassenstufe !== klassenstufe) {

        switch (klassenstufe) {
          case 'EINS': this.klassenstufeLabels.push('Klasse 1'); break;
          case 'IKID': this.klassenstufeLabels.push('Inklusion'); break;
          case 'ZWEI': this.klassenstufeLabels.push('Klasse 2'); break;
        }
      }
    }
  }
}
