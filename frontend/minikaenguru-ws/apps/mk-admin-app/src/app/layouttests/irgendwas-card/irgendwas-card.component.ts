import { Component, OnInit, Input } from '@angular/core';
import { Irgendwas } from '../layouttests.model';

@Component({
	selector: 'mka-irgendwas-card',
	templateUrl: './irgendwas-card.component.html',
	styleUrls: ['./irgendwas-card.component.css']
})
export class IrgendwasCardComponent implements OnInit {

	@Input()
	irgendwas: Irgendwas;

	constructor() { }

	ngOnInit(): void {
	}

}
