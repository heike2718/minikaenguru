import { Component, OnDestroy, OnInit } from '@angular/core';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { Observable, Subscription } from 'rxjs';
import { UploadsFacade } from '../uploads.facade';
import { UploadMonitoringInfo } from '../uploads.model';
import { initialPaginationComponentModel, PaginationComponentModel } from '@minikaenguru-ws/common-components';

@Component({
  selector: 'mka-uploads-list',
  templateUrl: './uploads-list.component.html',
  styleUrls: ['./uploads-list.component.css']
})
export class UploadsListComponent implements OnInit, OnDestroy {

  devMode = environment.envName === 'DEV';  

  paginationComponentModel!: PaginationComponentModel;

  private pageSize = 5;

  private anzahlUploadsSubscription: Subscription = new Subscription();

  constructor(public uploadsFacade: UploadsFacade) { }

  ngOnInit(): void {

    this.anzahlUploadsSubscription = this.uploadsFacade.anzahlUploads$.subscribe(

      anzahlUploads => {
        this.paginationComponentModel = {...initialPaginationComponentModel, collectionSize: anzahlUploads, pageSize: this.pageSize};
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
