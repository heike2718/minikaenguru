import { Component, OnInit } from '@angular/core';
import { AnmeldungenFacade } from '../anmeldungen.facade';

@Component({
	selector: 'mkod-anmeldungen-list',
	templateUrl: './anmeldungen-list.component.html',
	styleUrls: ['./anmeldungen-list.component.css']
})
export class AnmeldungenListComponent implements OnInit {

	constructor(public anmeldungenFacade: AnmeldungenFacade) { }

	ngOnInit(): void {

		this.anmeldungenFacade.loadAll();
	}

}
