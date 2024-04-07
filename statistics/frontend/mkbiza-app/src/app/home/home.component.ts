import { Component, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { WettbewerbeOverviewComponent } from '../wettbewerbe/wettbewerbe-overview/wettbewerbe-overview.component';
import { JahreAnzahlKinderComponent } from '../wettbewerbe/wettbewerbe-overview/anzahl-kinder/jahre-anzahl-kinder.component';
import { Configuration } from '@mkbiza-app/config';
import { DomainFacade } from '../shared/domain/api/src/lib/domain-facade.service';

@Component({
  selector: 'mkbiza-home',
  standalone: true,
  imports: [
    CommonModule,
    WettbewerbeOverviewComponent,
    JahreAnzahlKinderComponent,
    NgIf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {

  #configService = inject(Configuration);

  domainFacade = inject(DomainFacade);
  
  imageSourceLogo = this.#configService.assetsPath + 'mja_logo_2.svg';
}
