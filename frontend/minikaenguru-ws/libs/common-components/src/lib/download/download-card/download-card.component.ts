import { Component, OnInit, Input } from '@angular/core';
import { DownloadFacade } from '../download.facade';

@Component({
	selector: 'mk-download-card',
	templateUrl: './download-card.component.html',
	styleUrls: ['./download-card.component.css']
})
export class DownloadCardComponent implements OnInit {

	downloadInProgress$ = this.downloadFacade.downloadInProgress$;

	@Input()
	url: string;

	@Input()
	dateiname: string;

	@Input()
	mimetype: string;

	@Input()
	cardTitle: string;

	@Input()
	subtext: string;

	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {
	}

	startDownload(): void {
		this.downloadFacade.downloadFile(this.url, this.dateiname, this.mimetype);
	}

}
