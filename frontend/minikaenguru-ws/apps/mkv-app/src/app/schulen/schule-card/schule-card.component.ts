import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';
import { SchulenFacade } from '../schulen.facade';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	schule: Schule;


	constructor(private router: Router, private schulenFacade: SchulenFacade) { }

	ngOnInit(): void {
	}


	selectSchule(): void {
		this.schulenFacade.loadDetails(this.schule);
		this.router.navigateByUrl('/schulen/schule-dashboard/' + this.schule.kuerzel);
	}

}
