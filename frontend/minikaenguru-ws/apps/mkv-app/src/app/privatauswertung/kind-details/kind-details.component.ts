import { Component, OnInit, Input } from '@angular/core';
import { Kind } from '@minikaenguru-ws/common-components';
import { Privatveranstalter } from '../../wettbewerb/wettbewerb.model';

@Component({
	selector: 'mkv-kind-details',
	templateUrl: './kind-details.component.html',
	styleUrls: ['./kind-details.component.css']
})
export class KindDetailsComponent implements OnInit {


	@Input()
	kind: Kind

	@Input()
	veranstalter: Privatveranstalter;

	titel: string;

	constructor() { }

	ngOnInit(): void {

		this.titel = this.createTitel();
	}


	private createTitel(): string {

		let result = this.kind.vorname;

		if (this.kind.nachname) {
			result = result + ' ' + this.kind.nachname;
		}
		if (this.kind.zusatz) {
			result = result + ' (' + this.kind.zusatz + ')'
		}

		return result;
	}

}
