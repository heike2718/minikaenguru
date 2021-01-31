import { Component, OnInit, Input } from '@angular/core';
import { DownloadButtonModel } from '../download/download.model';
import { DownloadFacade } from '../download/download.facade';

@Component({
	selector: 'mk-unterlagen',
	templateUrl: './unterlagen.component.html',
	styleUrls: ['./unterlagen.component.css']
})
export class UnterlagenComponent implements OnInit {

	@Input()
	downloadUrl: string;

	@Input()
	userIdRef: string;

	@Input()
	sprache: string;

	showDownloadButton = false;

	unterlagenBtnModel: DownloadButtonModel;

	constructor(public downloadFacade: DownloadFacade) { }

	ngOnInit(): void {

		this.unterlagenBtnModel = {
			id: this.userIdRef,
			url: this.downloadUrl,
			dateiname: 'test',
			mimetype: 'zip',
			buttonLabel: this.sprache,
			tooltip: 'Unterlagen ' + this.sprache + ' herunterladen (ZIP)',
			class: 'btn btn-outline-dark'
		};

		this.showDownloadButton = true;

	}
}
