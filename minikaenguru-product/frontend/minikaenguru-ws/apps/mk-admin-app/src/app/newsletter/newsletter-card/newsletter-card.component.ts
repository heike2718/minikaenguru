import { Component, OnInit, Input, inject } from '@angular/core';
import { Newsletter, Empfaengertyp, NewsletterVersandauftrag } from '../../shared/newsletter-versandauftrage.model';
import { environment } from '../../../environments/environment';
import { NewsletterFacade } from '../newsletter.facade';
import { VersandauftraegeFacade } from '../../versandauftraege/versandauftraege.facade';

@Component({
	selector: 'mka-newsletter-card',
	templateUrl: './newsletter-card.component.html',
	styleUrls: ['./newsletter-card.component.css']
})
export class NewsletterCardComponent implements OnInit {

	devMode = environment.envName === 'DEV'

	@Input()
	newsletter!: Newsletter;

	sendMailpartVisible: boolean = false;

	empfaengertyp: string = '';
	aktuellAngemeldete = false;

	newsletterFacade = inject(NewsletterFacade);
	#versandauftraegeFacade = inject(VersandauftraegeFacade);

	ngOnInit(): void { }

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
				emfaengertyp: this.empfaengertyp as Empfaengertyp,
				nurAngemeldeteVeranstalter: this.aktuellAngemeldete
			};

			this.#versandauftraegeFacade.scheduleMailversand(auftrag);
			this.sendMailpartVisible = false;
		}
	}

	deleteNewsletter(): void {

		if (this.newsletter) {
			this.newsletterFacade.deleteNewsletter(this.newsletter);
		}
	}

	onChangeEmpfaengertyp($event: any): void {

		this.empfaengertyp = $event.target.value;
	}

	onCheckboxChanged() {

	}

}
