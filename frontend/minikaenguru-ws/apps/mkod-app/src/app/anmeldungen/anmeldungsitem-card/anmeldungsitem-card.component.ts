import { Component, OnInit, Input } from '@angular/core';
import { Anmeldungsitem } from '../../shared/beteiligungen.model';

@Component({
	selector: 'mkod-anmeldungsitem-card',
	templateUrl: './anmeldungsitem-card.component.html',
	styleUrls: ['./anmeldungsitem-card.component.css']
})
export class AnmeldungsitemCardComponent implements OnInit {

	@Input()
	anmeldungsitem: Anmeldungsitem;

	constructor() { }

	ngOnInit(): void {
	}

}
