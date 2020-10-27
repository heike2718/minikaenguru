import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { PrivatauswertungFacade } from '../privatauswertung.facade';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';

@Component({
	selector: 'mkv-kinder-list',
	templateUrl: './kinder-list.component.html',
	styleUrls: ['./kinder-list.component.css']
})
export class KinderListComponent implements OnInit {


	devMode = !environment.production;

	kinder$ = this.privatauswertungFacade.kinder$;

	veranstalter$ = this.veranstalterFacade.veranstalter$;

	constructor(private privatauswertungFacade: PrivatauswertungFacade,
		private veranstalterFacade: PrivatveranstalterFacade) { }

	ngOnInit(): void {}

}
