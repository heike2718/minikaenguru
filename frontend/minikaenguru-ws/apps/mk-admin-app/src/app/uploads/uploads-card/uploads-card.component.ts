import { Component, Input, OnInit } from '@angular/core';
import { DownloadButtonModel } from '@minikaenguru-ws/common-components';
import { environment } from 'apps/mk-admin-app/src/environments/environment';
import { initialDownloadButtonModel } from 'libs/common-components/src/lib/download/download.model';
import { SchulteilnahmenFacade } from '../../schulteilnahmen/schulteilnahmen.facade';
import { UploadMonitoringInfo } from '../uploads.model';

@Component({
  selector: 'mka-uploads-card',
  templateUrl: './uploads-card.component.html',
  styleUrls: ['./uploads-card.component.css']
})
export class UploadsCardComponent implements OnInit {

	devMode = environment.envName === 'DEV';

	@Input()
  	uploadInfo!: UploadMonitoringInfo;

  	fehlerreportDownloadButtonModel: DownloadButtonModel = initialDownloadButtonModel;
  	fileDownloadButtonModel: DownloadButtonModel = initialDownloadButtonModel;

  	constructor(private schulteilnahmenFacade: SchulteilnahmenFacade) { }

  	ngOnInit() {

	this.fehlerreportDownloadButtonModel =  {
			id: this.uploadInfo.uuid + '-fehlerreport',
			url: environment.apiUrl + '/uploads/' + this.uploadInfo.uuid + '/fehlerreport',
			buttonLabel: 'Fehlerreport',
			dateiname: this.uploadInfo.teilnahmenummer + '-' + this.uploadInfo.uuid + '-fehlerreport.csv',
			mimetype: 'application/octet-stream',
			tooltip: 'Fehlerreport herunterladen',
			class: 'btn btn-outline-dark w-100 ml-1'
		};

    this.fileDownloadButtonModel =  {
			id: this.uploadInfo.uuid + '-file',
			url: environment.apiUrl + '/uploads/' + this.uploadInfo.uuid + '/file',
			buttonLabel: 'Datei',
			dateiname: this.uploadInfo.fileName,
			mimetype: 'application/octet-stream',
			tooltip: 'Datei herunterladen',
			class: 'btn btn-outline-dark w-100 ml-1'
		};
  	}

	showDownloadButtons(): boolean {

		return this.uploadInfo.uploadStatus === 'DATENFEHLER' || this.uploadInfo.uploadStatus === 'HOCHGELADEN';
	}



  	onSchuleClicked() {

    	this.schulteilnahmenFacade.findOrLoadSchuleAdminOverview(this.uploadInfo.teilnahmenummer);
  	}
}
