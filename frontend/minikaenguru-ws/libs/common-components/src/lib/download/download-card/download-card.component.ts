import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { DownloadFacade } from '../download.facade';
import { DownloadCardModel } from '../download.model';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mk-download-card',
	templateUrl: './download-card.component.html',
	styleUrls: ['./download-card.component.css']
})
export class DownloadCardComponent implements OnInit, OnDestroy {

	downloadInProgress$ = this.downloadFacade.downloadInProgress$;

	private clickEventDisabled: boolean;

	private downloadProgressSubscription: Subscription;

	@Input()
	model: DownloadCardModel;

	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {

		this.downloadProgressSubscription = this.downloadInProgress$.subscribe(
			progress => this.clickEventDisabled = progress
		);

	}

	ngOnDestroy(): void {

		if (this.downloadProgressSubscription) {
			this.downloadProgressSubscription.unsubscribe();
		}

	}

	startDownload(): void {

		if (!this.clickEventDisabled) {
			this.downloadFacade.downloadFile(this.model.id, this.model.url, this.model.dateiname, this.model.mimetype);
		}
	}

}
