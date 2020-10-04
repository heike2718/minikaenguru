import { Component, OnInit } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';
import { VeranstalterSuchkriterium, VeranstalterSuchanfrage } from '../veranstalter.model';

@Component({
	selector: 'mka-veranstalter-suche',
	templateUrl: './veranstalter-suche.component.html',
	styleUrls: ['./veranstalter-suche.component.css']
})
export class VeranstalterSucheComponent implements OnInit {

	suchkriterien: VeranstalterSuchkriterium[] = ['EMAIL', 'NAME', 'TEILNAHMENUMMER', 'UUID'];

	model: VeranstalterSuchanfrage = {suchkriterium: undefined, suchstring: undefined};

	constructor(private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {
	}

	onSubmit(): void {

		this.veranstalterFacade.sucheVeranstalter(this.model);
	}

}
