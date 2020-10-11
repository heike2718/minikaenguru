import { Component, OnInit, Input } from '@angular/core';
import { Teilnahme } from '../common-components.model';
import { DownloadButtonModel } from '../download/download.model';


@Component({
	selector: 'mk-anonymisierte-teilnahme',
	templateUrl: './anonymisierte-teilnahme.component.html',
	styleUrls: ['./anonymisierte-teilnahme.component.css']
})
export class AnonymisierteTeilnahmeComponent implements OnInit {


	@Input()
	teilnahme: Teilnahme;

	@Input()
	statistikUrlPrefix: string;

	statistikBtnModel: DownloadButtonModel;

	showDownloadButton = false;


	constructor() { }

	ngOnInit(): void {

		if (this.teilnahme && this.teilnahme.anzahlKinder > 0) {

			this.statistikBtnModel = {
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
