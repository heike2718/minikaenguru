import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';

@Component({
	selector: 'mkod-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {

	version = environment.version;

	constructor(private router: Router) { }

	ngOnInit(): void {
	}	

	gotoWettbewerbe(): void {
		this.router.navigateByUrl('/wettbewerbe');
	}
	
	gotoAnmeldungen(): void {
		this.router.navigateByUrl('/anmeldungen');
	}

}
