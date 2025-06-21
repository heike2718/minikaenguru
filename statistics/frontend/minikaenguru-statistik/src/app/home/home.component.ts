import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WettbewerbeOverviewComponent } from '../wettbewerbe/wettbewerbe-overview/wettbewerbe-overview.component';
import { Configuration } from '@mks/config';

@Component({
    selector: 'mks-home',
    imports: [
        CommonModule,
        WettbewerbeOverviewComponent
    ],
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent {

  #configService = inject(Configuration);  
  imageSourceLogo = this.#configService.assetsPath + 'mja_logo_2.svg';
}
