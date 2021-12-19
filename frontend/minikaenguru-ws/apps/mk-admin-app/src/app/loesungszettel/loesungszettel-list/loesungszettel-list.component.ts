import { Component, OnDestroy, OnInit } from '@angular/core';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { LoesungszettelFacade } from '../loesungszettel.facade';
import { initialPaginationComponentModel, PaginationComponentModel } from '@minikaenguru-ws/common-components';
import { Subscription } from 'rxjs';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
  selector: 'mka-loesungszettel-list',
  templateUrl: './loesungszettel-list.component.html',
  styleUrls: ['./loesungszettel-list.component.css']
})
export class LoesungszettelListComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';
  
  jahr: number | undefined;

  paginationComponentModel!: PaginationComponentModel;

  anzahl?: number;

  private pageSize = 5;

  private anzahlSubscription: Subscription = new Subscription();

  constructor(public loesungszettelFacade: LoesungszettelFacade, private logger: LogService) { }

  ngOnInit(): void { 

    this.anzahlSubscription = this.loesungszettelFacade.anzahl$.subscribe(
      anzahl => {
        this.anzahl = anzahl;
        this.logger.debug('init paginator with collectionSize=' + anzahl)
        this.paginationComponentModel = {...initialPaginationComponentModel, collectionSize: anzahl}
      }
    );
  }

  ngOnDestroy(): void {
      this.anzahlSubscription.unsubscribe();
  }

  onJahrChanged(): void {

    this.loesungszettelFacade.selectJahr(this.jahr, this.pageSize);

  }

  onPageChanged(page: number): void {
    this.loesungszettelFacade.getOrLoadPage(page, this.pageSize);
	}
}
