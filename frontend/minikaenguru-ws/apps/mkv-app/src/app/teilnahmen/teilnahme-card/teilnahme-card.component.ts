import { Component, OnInit, Input } from '@angular/core';
import { AnonymisierteTeilnahme } from '../../wettbewerb/wettbewerb.model';
import { environment } from '../../../environments/environment';
import { DownloadButtonModel } from '@minikaenguru-ws/common-components';

@Component({
	selector: 'mkv-teilnahme-card',
	templateUrl: './teilnahme-card.component.html',
	styleUrls: ['./teilnahme-card.component.css']
})
export class TeilnahmeCardComponent implements OnInit {



	devMode = !environment.production;

	@Input()
	teilnahme: AnonymisierteTeilnahme;

	statistikBtnModel: DownloadButtonModel;

	showDownloadButton = false;

	constructor() { }

	ngOnInit(): void {

		if (this.teilnahme && this.teilnahme.anzahlKinder > 0) {

			this.statistikBtnModel = {
				url: environment.apiUrl + '/statistik/' + this.teilnahme.identifier.teilnahmeart + '/' + this.teilnahme.identifier.teilnahmenummer + '/' + this.teilnahme.identifier.jahr,
				dateiname: 'test',
				mimetype: 'pdf',
				buttonLabel: 'Statistik',
				tooltip: 'Statistik generieren und herunterladen (PDF)'
			};

			this.showDownloadButton = true;
		}
	}


}
