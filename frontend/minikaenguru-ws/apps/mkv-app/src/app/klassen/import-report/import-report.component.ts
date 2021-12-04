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

  devMode = environment.envName === 'DEV';

  importReportDownloadModel!: DownloadButtonModel;

  importReportSubscription: Subscription = new Subscription();

  constructor(public klassenFacade: KlassenFacade) { }

  ngOnInit() {

    this.importReportSubscription = this.klassenFacade.klassenimportReport$.subscribe( r => {
      if (r) {
        this.importReportDownloadModel = {
          id: 'import-report-' + r.id.substr(0,8),
          url: environment.apiUrl + '/klassen/importreport/' + r.id,
          dateiname: 'importreport-' + r?.id + '.csv',
          mimetype: 'csv',
          buttonLabel: 'Fehlerreport speichern',
          tooltip: 'Fehlerreport speichern',
          class: 'btn btn-outline-dark w-100 ml-1'
        };
      }
    });
  }

  ngOnDestroy() {

    this.importReportSubscription.unsubscribe();
  }

}
