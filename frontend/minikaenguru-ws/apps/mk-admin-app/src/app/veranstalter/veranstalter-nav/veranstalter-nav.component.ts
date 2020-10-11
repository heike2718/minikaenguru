import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { VeranstalterFacade } from '../veranstalter.facade';

@Component({
	selector: 'mka-veranstalter-nav',
	templateUrl: './veranstalter-nav.component.html',
	styleUrls: ['./veranstalter-nav.component.css']
})
export class VeranstalterNavComponent implements OnInit {

	constructor(private router: Router, private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {
	}



	gotoVeranstalter(): void {
		this.veranstalterFacade.clearVeranstalterSelection();
	}

	gotoDashboard(): void {
		this.veranstalterFacade.trefferlisteLeeren();
		this.router.navigateByUrl('/dashboard');
	}

}
