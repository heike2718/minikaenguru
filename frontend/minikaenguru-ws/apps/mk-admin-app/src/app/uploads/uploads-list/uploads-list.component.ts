import { Component, OnDestroy, OnInit } from '@angular/core';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { combineLatest, Subscription } from 'rxjs';
import { UploadsFacade } from '../uploads.facade';
import { initialPaginationComponentModel, PaginationComponentModel } from '@minikaenguru-ws/common-components';
import { LogService } from '@minikaenguru-ws/common-logging';
import { anzahlUploads } from '../+state/uploads.selectors';

@Component({
  selector: 'mka-uploads-list',
  templateUrl: './uploads-list.component.html',
  styleUrls: ['./uploads-list.component.css']
})
export class UploadsListComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';  

  paginationComponentModel!: PaginationComponentModel;

  anzahl = 0;

  pageSize = 5;

  sizeLoaded = false;

  private anzahlUploadsSubscription: Subscription = new Subscription();

  constructor(public uploadsFacade: UploadsFacade, private logger: LogService) { }

  ngOnInit(): void {

    this.anzahlUploadsSubscription = this.uploadsFacade.anzahlUploads$.subscribe(

      anzahl => {
        this.paginationComponentModel = {...initialPaginationComponentModel, collectionSize: anzahl};
        this.uploadsFacade.getOrLoadNextPage(1, this.pageSize);
      }
    );
  }

  ngOnDestroy(): void {

    this.anzahlUploadsSubscription.unsubscribe();

  }

  onPageChanged(page: number): void {
    this.uploadsFacade.getOrLoadNextPage(page, this.pageSize);   
	}
}
