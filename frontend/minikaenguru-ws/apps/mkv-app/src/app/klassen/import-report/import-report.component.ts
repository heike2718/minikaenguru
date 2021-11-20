import { Component, OnDestroy, OnInit } from '@angular/core';
import { DownloadButtonModel } from '@minikaenguru-ws/common-components';
import { environment } from 'apps/mkv-app/src/environments/environment';
import { Subscription } from 'rxjs';
import { KlassenFacade } from '../klassen.facade';

@Component({
  selector: 'mkv-import-report',
  templateUrl: './import-report.component.html',
  styleUrls: ['./import-report.component.css']
})
export class ImportReportComponent implements OnInit, OnDestroy {

  devMode = !environment.production;

  importReportDownloadModel!: DownloadButtonModel;

  importReportSubscription: Subscription = new Subscription();

  constructor(public klassenFacade: KlassenFacade) { }

  ngOnInit() {

    this.importReportSubscription = this.klassenFacade.klassenimportReport$.subscribe( r => {
      this.importReportDownloadModel = {
        id: '',
        url: environment.apiUrl + '/klassen/importreport/{uuid}',
        dateiname: 'importreport-' + r?.id + '.csv',
        mimetype: 'csv',
        buttonLabel: 'speichern',
        tooltip: 'Fehlerreport speichern',
        class: 'btn btn-outline-dark w-100 ml-1'
      };
    });
  }

  ngOnDestroy() {

    this.importReportSubscription.unsubscribe();
  }

}
