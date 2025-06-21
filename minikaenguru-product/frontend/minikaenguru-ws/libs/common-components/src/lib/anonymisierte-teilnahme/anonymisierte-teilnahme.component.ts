import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { Teilnahme } from '../common-components.model';
import { DownloadButtonModel, initialDownloadButtonModel } from '../download/download.model';
import { DownloadFacade } from '../download/download.facade';


@Component({
	selector: 'mk-anonymisierte-teilnahme',
	templateUrl: './anonymisierte-teilnahme.component.html',
	styleUrls: ['./anonymisierte-teilnahme.component.css']
})
export class AnonymisierteTeilnahmeComponent implements OnInit {

	@Input()
	teilnahme!: Teilnahme;

	@Input()
	statistikUrlPrefix!: string;

	@Input()
	showUploadButton!: boolean;

	@Output()
	uploadButtonClicked: EventEmitter<Teilnahme> = new EventEmitter<Teilnahme>();

	statistikBtnModel: DownloadButtonModel = initialDownloadButtonModel;

	showDownloadButton = false;

	downloadId: string = '';


	constructor(public downloadFacade: DownloadFacade) { }

	ngOnInit(): void {

		this.downloadId = this.teilnahme.identifier.teilnahmenummer + '-' + this.teilnahme.identifier.jahr;

		if (this.teilnahme && this.teilnahme.anzahlKinder > 0) {

			this.statistikBtnModel = {
				id: this.downloadId,
				url: this.statistikUrlPrefix + this.teilnahme.identifier.teilnahmeart + '/' + this.teilnahme.identifier.teilnahmenummer + '/' + this.teilnahme.identifier.jahr,
				dateiname: 'test',
				mimetype: 'pdf',
				buttonLabel: 'Statistik',
				tooltip: 'Statistik generieren und herunterladen (PDF)',
				class: 'btn btn-outline-dark w-100 ml-1'
			};

			this.showDownloadButton = true;
		}
	}

	onUploadClicked(): void {
		this.uploadButtonClicked.emit(this.teilnahme);
	}
}
