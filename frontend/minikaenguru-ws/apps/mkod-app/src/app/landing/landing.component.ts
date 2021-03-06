import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
	selector: 'mkod-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {

	constructor(private router: Router) { }

	ngOnInit(): void {
	}

	gotoAnmeldungen() {
		this.router.navigateByUrl('anmeldungen');
	}

}
