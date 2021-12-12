import { Component, OnDestroy, OnInit } from '@angular/core';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { LoesungszettelFacade } from '../loesungszettel.facade';
import { initialPaginationComponentModel, PaginationComponentModel } from '@minikaenguru-ws/common-components';
import { Subscription } from 'rxjs';

@Component({
  selector: 'mka-loesungszettel-list',
  templateUrl: './loesungszettel-list.component.html',
  styleUrls: ['./loesungszettel-list.component.css']
})
export class LoesungszettelListComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';
  
  jahr: number | undefined;

  paginationComponentModel!: PaginationComponentModel;

  private pageSize = 5;

  private anzahlSubscription: Subscription = new Subscription();

  constructor(public loesungszettelFacade: LoesungszettelFacade) { }

  ngOnInit(): void { 

    this.anzahlSubscription = this.loesungszettelFacade.anzahl$.subscribe(
      anzahl => {
        this.paginationComponentModel = {...initialPaginationComponentModel, collectionSize: anzahl, pageSize: this.pageSize}
      }
    );
  }

  ngOnDestroy(): void {
      this.anzahlSubscription.unsubscribe();
  }

  onJahrChanged(): void {

    this.loesungszettelFacade.selectJahr(this.jahr);

  }

  onPageChanged(page: number): void {
    this.loesungszettelFacade.getOrLoadPage(page, this.pageSize);
	}
}
