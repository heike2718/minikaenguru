import { Component, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { WettbewerbeOverviewComponent } from '../wettbewerbe/wettbewerbe-overview/wettbewerbe-overview.component';
import { JahreAnzahlKinderComponent } from '../wettbewerbe/wettbewerbe-overview/anzahl-kinder/jahre-anzahl-kinder.component';
import { Configuration } from '@mkbiza-app/config';
import { JahreMedianeComponent } from '../wettbewerbe/wettbewerbe-overview/mediane/jahre-mediane.component';
import { JahreKinderKlassenstufeComponent } from '../wettbewerbe/wettbewerbe-overview/kinder-klassenstufe/jahre-kinder-klassenstufe.component';

@Component({
  selector: 'mkbiza-home',
  standalone: true,
  imports: [
    CommonModule,
    WettbewerbeOverviewComponent,
    JahreAnzahlKinderComponent,
    JahreMedianeComponent,
    JahreKinderKlassenstufeComponent,
    NgIf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {

  #configService = inject(Configuration);  
  imageSourceLogo = this.#configService.assetsPath + 'mja_logo_2.svg';
}
