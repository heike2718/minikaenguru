import { Component, OnInit, Input } from '@angular/core';
import { Teilnahme } from '../common-components.model';
import { DownloadButtonModel } from '../download/download.model';
import { DownloadFacade } from '../download/download.facade';


@Component({
	selector: 'mk-anonymisierte-teilnahme',
	templateUrl: './anonymisierte-teilnahme.component.html',
	styleUrls: ['./anonymisierte-teilnahme.component.css']
})
export class AnonymisierteTeilnahmeComponent implements OnInit {

	downloadId$ = this.downloadFacade.downloadId$;

	@Input()
	teilnahme: Teilnahme;

	@Input()
	statistikUrlPrefix: string;

	statistikBtnModel: DownloadButtonModel;

	showDownloadButton = false;

	downloadId: string;


	constructor(private downloadFacade: DownloadFacade) { }

	ngOnInit(): void {

		this.downloadId = this.teilnahme.identifier.teilnahmenummer + '-' + this.teilnahme.identifier.jahr;

		if (this.teilnahme && this.teilnahme.anzahlKinder > 0) {

			this.statistikBtnModel = {
				id: this.downloadId,
				url: this.statistikUrlPrefix + this.teilnahme.identifier.teilnahmeart + '/' + this.teilnahme.identifier.teilnahmenummer + '/' + this.teilnahme.identifier.jahr,
				dateiname: 'test',
				mimetype: 'pdf',
				buttonLabel: 'Statistik',
				tooltip: 'Statistik generieren und herunterladen (PDF)'
			};

			this.showDownloadButton = true;
		}
	}

}