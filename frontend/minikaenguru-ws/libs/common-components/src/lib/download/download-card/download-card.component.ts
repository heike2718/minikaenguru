import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { DownloadFacade } from '../download.facade';
import { DownloadCardModel, initialDownloadCardModel } from '../download.model';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mk-download-card',
	templateUrl: './download-card.component.html',
	styleUrls: ['./download-card.component.css']
})
export class DownloadCardComponent implements OnInit, OnDestroy {

	downloadInProgress$ = this.downloadFacade.downloadInProgress$;

	private clickEventDisabled = false;

	private downloadProgressSubscription: Subscription = new Subscription();

	@Input()
	model: DownloadCardModel = initialDownloadCardModel;

	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {

		this.downloadProgressSubscription = this.downloadInProgress$.subscribe(
			progress => this.clickEventDisabled = progress
		);
	}

	ngOnDestroy(): void {
		this.downloadProgressSubscription.unsubscribe();
	}

	startDownload(): void {

		if (!this.clickEventDisabled) {
			this.downloadFacade.downloadFile(this.model.id, this.model.url, this.model.dateiname, this.model.mimetype);
		}
	}

}
