import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WettbewerbeOverviewComponent } from '../wettbewerbe/wettbewerbe-overview/wettbewerbe-overview.component';
import { JahreAnzahlKinderComponent } from '../wettbewerbe/wettbewerbe-overview/anzahl-kinder/jahre-anzahl-kinder.component';

@Component({
  selector: 'mkbiza-home',
  standalone: true,
  imports: [
    CommonModule,
    WettbewerbeOverviewComponent,
    JahreAnzahlKinderComponent
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  
  imageSourceLogo = '';
}
