import { Component, OnInit, Input } from '@angular/core';
import { DownloadFacade } from '../download.facade';
import { DownloadCardModel } from '../download.model';

@Component({
	selector: 'mk-download-card',
	templateUrl: './download-card.component.html',
	styleUrls: ['./download-card.component.css']
})
export class DownloadCardComponent implements OnInit {

	downloadInProgress$ = this.downloadFacade.downloadInProgress$;

	@Input()
	model: DownloadCardModel;

	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {
	}

	startDownload(): void {
		this.downloadFacade.downloadFile(this.model.url, this.model.dateiname, this.model.mimetype);
	}

}
