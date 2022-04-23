import { Component, OnInit } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';
import { VeranstalterSuchkriterium, VeranstalterSuchanfrage } from '../veranstalter.model';

@Component({
	selector: 'mka-veranstalter-suche',
	templateUrl: './veranstalter-suche.component.html',
	styleUrls: ['./veranstalter-suche.component.css']
})
export class VeranstalterSucheComponent implements OnInit {

	suchkriterien: VeranstalterSuchkriterium[] = ['EMAIL', 'NAME', 'TEILNAHMENUMMER', 'UUID', 'ZUGANGSSTATUS'];

	model: VeranstalterSuchanfrage = {suchkriterium: 'EMAIL', suchstring: undefined};

	constructor(private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {
	}

	onSubmit(): void {

		if (this.model.suchkriterium === 'ZUGANGSSTATUS') {

			this.veranstalterFacade.sucheVeranstalter({...this.model, suchstring: this.model.suchstring?.toUpperCase()});

		} else {

			this.veranstalterFacade.sucheVeranstalter(this.model);
		}
	}

}
