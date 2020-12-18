import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';

@Component({
	selector: 'mka-newsletter-list',
	templateUrl: './newsletter-list.component.html',
	styleUrls: ['./newsletter-list.component.css']
})
export class NewsletterListComponent implements OnInit {

	devMode = environment.envName === 'DEV'

	constructor(private router: Router) { }

	ngOnInit(): void {

		console.log('entered NewsletterListComponent');
	}


	addNewsletter(): void {

		console.log('jetzt neues NewsletterEditorModel erzeugen und zum Editor navigieren.');
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
