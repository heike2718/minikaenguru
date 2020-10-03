import { Component, OnInit } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';

@Component({
	selector: 'mka-veranstalter-suche',
	templateUrl: './veranstalter-suche.component.html',
	styleUrls: ['./veranstalter-suche.component.css']
})
export class VeranstalterSucheComponent implements OnInit {

	constructor(veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {
	}

}
