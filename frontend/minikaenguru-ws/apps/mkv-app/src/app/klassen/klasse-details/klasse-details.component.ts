import { Component, OnInit, Input } from '@angular/core';
import { Klasse } from '@minikaenguru-ws/common-components';

@Component({
	selector: 'mkv-klasse-details',
	templateUrl: './klasse-details.component.html',
	styleUrls: ['./klasse-details.component.css']
})
export class KlasseDetailsComponent implements OnInit {

	@Input()
	klasse: Klasse;

	constructor() { }

	ngOnInit(): void { }

}
