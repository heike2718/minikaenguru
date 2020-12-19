import { Component, OnInit } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { NewsletterFacade } from '../newsletter.facade';

@Component({
	selector: 'mka-newsletter-list',
	templateUrl: './newsletter-list.component.html',
	styleUrls: ['./newsletter-list.component.css']
})
export class NewsletterListComponent implements OnInit {

	devMode = environment.envName === 'DEV'

	constructor(private router: Router,
		public newsletterFacade: NewsletterFacade) { }

	ngOnInit(): void {

		console.log('entered NewsletterListComponent');
	}

	loadNewsletters(): void {
		this.newsletterFacade.loadNewsletters();
	}

	addNewsletter(): void {
		this.newsletterFacade.createNewNewsletter();
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
