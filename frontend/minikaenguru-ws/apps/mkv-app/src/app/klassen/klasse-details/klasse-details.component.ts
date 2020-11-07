import { Component, OnInit, Input } from '@angular/core';
import { Klasse } from '@minikaenguru-ws/common-components';
import { Schule } from '../../lehrer/schulen/schulen.model';

@Component({
	selector: 'mkv-klasse-details',
	templateUrl: './klasse-details.component.html',
	styleUrls: ['./klasse-details.component.css']
})
export class KlasseDetailsComponent implements OnInit {

	@Input()
	schule: Schule;

	@Input()
	klasse: Klasse;



	constructor() { }

	ngOnInit(): void {
	}

}
