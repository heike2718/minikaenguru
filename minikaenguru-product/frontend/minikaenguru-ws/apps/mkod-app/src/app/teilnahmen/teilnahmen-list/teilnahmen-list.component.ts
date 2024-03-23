import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LogService } from '@minikaenguru-ws/common-logging';
import { Subscription } from 'rxjs';
import { TeilnahmenFacade } from '../teilnahmen.facade';

@Component({
  selector: 'mkod-teilnahmen-list',
  templateUrl: './teilnahmen-list.component.html',
  styleUrls: ['./teilnahmen-list.component.css']
})
export class TeilnahmenListComponent implements OnInit, OnDestroy {

  private selectedAnmeldunngenSubscription: Subscription = new Subscription();

  constructor(public teilnahmenFacade: TeilnahmenFacade,
    private logger: LogService,
    private router: Router) { }

  ngOnInit(): void {

    this.selectedAnmeldunngenSubscription = this.teilnahmenFacade.selectedAnmeldung$.subscribe(
      a => {
        if (a) {
          this.logger.info('Wettbewerbsjahr=' + a.wettbewerbsjahr );
        } else {
          this.logger.info('anmeldung war leider undefined');
        }
      }
    );
  }

  ngOnDestroy(): void {
    this.selectedAnmeldunngenSubscription.unsubscribe();
  }

  gotoWettbewerbe(): void {
    this.router.navigateByUrl('/wettbewerbe');
  }

  gotoAnmeldungen(): void {
    this.router.navigateByUrl('/anmeldungen');
  }
}
