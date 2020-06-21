import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	schule: Schule;


	constructor(private router: Router, private lehrerFacade: LehrerFacade) { }

	ngOnInit(): void {
	}


	selectSchule(): void {
		this.lehrerFacade.selectSchule(this.schule);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

}
