import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { SchulenFacade } from '../schulen.facade';

@Component({
	selector: 'mkv-schulen-list',
	templateUrl: './schulen-list.component.html',
	styleUrls: ['./schulen-list.component.css']
})
export class SchulenListComponent implements OnInit {

	devMode = !environment.production;

	schulen$ = this.schulenFacade.schulen$;

	constructor(private schulenFacade: SchulenFacade, private router: Router) {
	}

	ngOnInit(): void {
	}

	addSchule(): void {
		console.log('hier neue Schule auswählen lassen')
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
