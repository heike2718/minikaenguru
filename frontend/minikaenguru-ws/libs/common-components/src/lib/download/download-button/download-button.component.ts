import { Component, OnInit, Input } from '@angular/core';
import { DownloadFacade } from '../download.facade';
import { DownloadButtonModel } from '../download.model';

@Component({
	selector: 'mk-download-button',
	templateUrl: './download-button.component.html',
	styleUrls: ['./download-button.component.css']
})
export class DownloadButtonComponent implements OnInit {

	downloadInProgress$ = this.downloadFacade.downloadInProgress$;

	@Input()
	model: DownloadButtonModel;

	// @Input()
	// url: string;

	// @Input()
	// dateiname: string;

	// @Input()
	// mimetype: string;

	// @Input()
	// buttonLabel: string;

	// @Input()
	// tooltip: string;

	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {
	}

	startDownload(): void {
		this.downloadFacade.downloadFile(this.model.url, this.model.dateiname, this.model.mimetype);
	}

}
