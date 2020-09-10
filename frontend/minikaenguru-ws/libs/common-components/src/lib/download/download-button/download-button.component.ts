import { Component, OnInit, Input } from '@angular/core';
import { DownloadFacade } from '../download.facade';

@Component({
	selector: 'mk-download-button',
	templateUrl: './download-button.component.html',
	styleUrls: ['./download-button.component.css']
})
export class DownloadButtonComponent implements OnInit {

	downloadInProgress$ = this.downloadFacade.downloadInProgress$;

	@Input()
	url: string;

	@Input()
	dateiname: string;

	@Input()
	mimetype: string;

	@Input()
	buttonLabel: string;

	@Input()
	tooltip: string;

	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {
	}

	startDownload(): void {
		this.downloadFacade.downloadFile(this.url, this.dateiname, this.mimetype);
	}

}
