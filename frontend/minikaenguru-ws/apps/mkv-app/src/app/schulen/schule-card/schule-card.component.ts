import { Component, OnInit, Input } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit {

	devMode = !environment.production;

	@Input()
	schule: Schule;


	constructor(private router: Router) { }

	ngOnInit(): void {
	}


	selectSchule(): void {
		console.log('navigieren zu schule-dashboard')
		this.router.navigate(['/schulen/schule-dashboard']);
	}

}
