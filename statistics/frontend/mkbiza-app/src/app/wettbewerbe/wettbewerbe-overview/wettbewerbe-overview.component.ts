import { Component, inject } from '@angular/core';
import { CommonModule, NgFor, NgIf } from '@angular/common';
import { DomainFacade } from '@mkbiza-app/domain-api';

@Component({
  selector: 'mkbiza-wettbewerbe',
  standalone: true,
  imports: [CommonModule,NgIf,NgFor],
  templateUrl: './wettbewerbe-overview.component.html',
  styleUrl: './wettbewerbe-overview.component.scss',
})
export class WettbewerbeOverviewComponent {

  domainFacade = inject(DomainFacade);

}
