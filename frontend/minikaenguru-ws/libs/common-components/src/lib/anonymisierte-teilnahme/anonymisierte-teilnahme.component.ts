import { Component, OnInit, Input } from '@angular/core';
import { Teilnahme } from '../common-components.model';


@Component({
	selector: 'mk-anonymisierte-teilnahme',
	templateUrl: './anonymisierte-teilnahme.component.html',
	styleUrls: ['./anonymisierte-teilnahme.component.css']
})
export class AnonymisierteTeilnahmeComponent implements OnInit {


	@Input()
	teilnahme: Teilnahme


	constructor() { }

	ngOnInit(): void {
	}

}
