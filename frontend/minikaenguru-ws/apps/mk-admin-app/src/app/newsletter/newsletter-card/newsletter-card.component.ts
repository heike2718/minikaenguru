import { Component, OnInit, Input } from '@angular/core';
import { Newsletter, Empfaengertyp, NewsletterVersandauftrag, Versandinfo } from '../newsletter.model';
import { environment } from '../../../environments/environment';
import { NewsletterFacade } from '../newsletter.facade';

@Component({
	selector: 'mka-newsletter-card',
	templateUrl: './newsletter-card.component.html',
	styleUrls: ['./newsletter-card.component.css']
})
export class NewsletterCardComponent implements OnInit {

	devMode = environment.envName === 'DEV'

	@Input()
	newsletter: Newsletter;

	sendMailpartVisible: boolean;

	empfaengertyp: string;

	constructor(public newsletterFacade: NewsletterFacade) { }

	ngOnInit(): void {
		this.sendMailpartVisible = false;
		this.empfaengertyp = undefined;
	}

	editNewsletter(): void {
		this.newsletterFacade.startEditNewsletter(this.newsletter);
	}

	toggleSendMail(): void {
		this.sendMailpartVisible = !this.sendMailpartVisible;
	}

	mailSenden(): void {

		if (this.empfaengertyp && this.empfaengertyp.length > 0) {

			const auftrag: NewsletterVersandauftrag = {
				newsletterID: this.newsletter.uuid,
				emfaengertyp: this.empfaengertyp as Empfaengertyp
			};

			this.newsletterFacade.scheduleMailversand(auftrag);
			this.sendMailpartVisible = false;
		}
	}

	deleteNewsletter(): void {
		this.newsletterFacade.deleteNewsletter(this.newsletter);
	}

	onChangeEmpfaengertyp($event): void {

		this.empfaengertyp = $event.target.value;

		console.log('neuer Empfaengertyp=' + this.empfaengertyp);
	}

}
