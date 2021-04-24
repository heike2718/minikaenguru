import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Router } from '@angular/router';
import { NewsletterFacade } from '../newsletter.facade';
import { Versandinfo } from '../newsletter.model';

@Component({
	selector: 'mka-newsletter-list',
	templateUrl: './newsletter-list.component.html',
	styleUrls: ['./newsletter-list.component.css']
})
export class NewsletterListComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV'

	constructor(private router: Router,
		public newsletterFacade: NewsletterFacade) { }

	ngOnInit(): void {

		const versandinfo: Versandinfo = {
			anzahlAktuellVersendet: 12,
			anzahlEmpaenger: 500,
			empfaengertyp: 'LEHRER',
			newsletterID: 'hallo',
			uuid: "bla",
			versandBegonnenAm: '24.04.2021 13:42',
			versandMitFehler: false
		};


		this.newsletterFacade.startPollVersandinfo(versandinfo);
	}

	ngOnDestroy(): void {
		this.newsletterFacade.stopPollVersandinfo();
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
