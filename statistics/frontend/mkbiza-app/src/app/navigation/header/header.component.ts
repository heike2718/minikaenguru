import { Component, EventEmitter, OnDestroy, OnInit, Output, inject } from '@angular/core';
import { CommonModule, NgIf } from '@angular/common';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar'
import { MatMenuModule } from '@angular/material/menu';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { Router, RouterLinkWithHref } from '@angular/router';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Subscription } from 'rxjs';

@Component({
  selector: 'mkbiza-header',
  standalone: true,
  imports: [
    CommonModule,
    MatMenuModule,
    MatIconModule,
    MatListModule,
    MatToolbarModule,
    MatTooltipModule,
    NgIf,
    RouterLinkWithHref
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent implements OnInit, OnDestroy {

  version = "1.1.0";
  menuIds: number[] = [];

  @Output()
  sidenavToggle = new EventEmitter();

  #breakpointObserver = inject(BreakpointObserver);
  #domainFacade = inject(DomainFacade);
  #router = inject(Router);

  #wettbewerbIDsSubscription = new Subscription();

  get isHandset(): boolean {
    return this.#breakpointObserver.isMatched(Breakpoints.Handset);
  }

  onToggleSidenav(): void {
    console.log('onToggleSidenav');
    this.sidenavToggle.emit();
  }

  onMenuItemClick(id: number): void {
    this.#router.navigate(['/wettbewerbe', id]);
  }

  ngOnInit(): void {

    this.#wettbewerbIDsSubscription = this.#domainFacade.wettbewerbIDs$.subscribe(
      (ids) => {
        this.menuIds = ids;
      }
    );
  }

  ngOnDestroy(): void {
    this.#wettbewerbIDsSubscription.unsubscribe();
  }

}
