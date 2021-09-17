import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AnmeldungenFacade } from '../anmeldungen.facade';

@Component({
	selector: 'mkod-anmeldungen-list',
	templateUrl: './anmeldungen-list.component.html',
	styleUrls: ['./anmeldungen-list.component.css']
})
export class AnmeldungenListComponent implements OnInit {

	constructor(public anmeldungenFacade: AnmeldungenFacade, private router: Router) { }

	ngOnInit(): void {

		this.anmeldungenFacade.loadAll();
	}

	gotoWettbewerbe(): void {
		this.router.navigateByUrl('/wettbewerbe');
	}

	gotoLanding(): void {
		this.router.navigateByUrl('/landing');
	}

	
}
