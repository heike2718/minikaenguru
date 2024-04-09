import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DomainFacade } from '@mkbiza-app/domain-api';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'mkbiza-wettbewerb',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './wettbewerb-details.component.html',
  styleUrl: './wettbewerb-details.component.scss',
})
export class WettbewerbDetailsComponent implements OnInit, OnDestroy {

  domainFacade = inject(DomainFacade);
  wettbewerbsjahr!: number;

  #activatedRoute = inject(ActivatedRoute);
  #routeSubscription = new Subscription();

  ngOnInit(): void {
    this.#routeSubscription = this.#activatedRoute.params.subscribe(params => {
      this.wettbewerbsjahr = params['id'];
      this.domainFacade.loadWettbewerbsdetails(this.wettbewerbsjahr);
    });
  }

  ngOnDestroy(): void {
    this.#routeSubscription.unsubscribe();
  }

}
