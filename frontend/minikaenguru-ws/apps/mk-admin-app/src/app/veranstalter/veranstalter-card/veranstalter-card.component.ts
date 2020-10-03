import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Veranstalter } from '../veranstalter.model';
import { VeranstalterFacade } from '../veranstalter.facade';

@Component({
	selector: 'mka-veranstalter-card',
	templateUrl: './veranstalter-card.component.html',
	styleUrls: ['./veranstalter-card.component.css']
})
export class VeranstalterCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	veranstalter: Veranstalter;
	veranstalterUndefined: boolean;


	constructor(private verastalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {

		this.veranstalterUndefined = !this.veranstalter;
	}

	selectVeranstalter(): void {
		if (this.veranstalter) {
			this.verastalterFacade.selectVeranstalter(this.veranstalter);
		}
	}

}
