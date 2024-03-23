import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { DownloadFacade } from '../download.facade';
import { DownloadButtonModel, initialDownloadButtonModel } from '../download.model';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mk-download-button',
	templateUrl: './download-button.component.html',
	styleUrls: ['./download-button.component.css']
})
export class DownloadButtonComponent implements OnInit, OnDestroy {

	downloadButtonDisabled = true;

	@Input()
	model: DownloadButtonModel = initialDownloadButtonModel;

	private downloadProgressSubscription: Subscription = new Subscription();

	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {

		this.downloadProgressSubscription = this.downloadFacade.downloadInProgress$.subscribe(
			progress => this.downloadButtonDisabled = progress
		);

	}

	ngOnDestroy(): void {
		this.downloadProgressSubscription.unsubscribe();
	}

	startDownload(): void {
		this.downloadFacade.downloadFile(this.model.id, this.model.url, this.model.dateiname, this.model.mimetype);
	}

}
