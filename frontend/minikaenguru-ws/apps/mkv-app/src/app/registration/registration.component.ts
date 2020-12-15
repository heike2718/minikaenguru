import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { SchulkatalogFacade, KatalogItem } from '@minikaenguru-ws/common-schulkatalog';
import { Subscription } from 'rxjs';
import { RegistrationFacade } from './registration.facade';

@Component({
	selector: 'mkv-registration',
	templateUrl: './registration.component.html',
	styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit, OnDestroy {

	devMode: boolean;

	selectedKatalogItem: KatalogItem;
	newsletterAbonnieren: boolean;
	textNewsletter: string;
	showInfoNewsletter: boolean;

	private selectedKatalogItemSubscription: Subscription;

	private newsletterAboStateSubscription: Subscription;

	constructor(private router: Router
		, public registrationFacade: RegistrationFacade
		, public schulkatalogFacade: SchulkatalogFacade) {

		this.devMode = environment.envName === 'DEV';

		this.textNewsletter = `In diesem Fall werden Sie über den Wettbewerb betreffende Änderungen per E-Mail informiert. Ihre Daten werden ausschließlich zu diesem Zweck genutzt. Eine Weitergabe an Dritte erfolgt nicht.
		  Sie können die Einwilligung jederzeit per E-Mail an minikaenguru@egladil.de oder nach dem Einloggen widerrufen.`

	}

	ngOnInit() {

		this.initState();

		this.registrationFacade.resetRegistrationState();

		this.schulkatalogFacade.initSchulkatalog('ORT');

		this.newsletterAboStateSubscription = this.registrationFacade.newsletterAboState$.subscribe(
			flag => this.newsletterAbonnieren = flag
		);

		this.selectedKatalogItemSubscription = this.schulkatalogFacade.selectedKatalogItem$.subscribe(
			item => {
				if (item) {
					this.selectedKatalogItem = item;
					if (item.typ === 'SCHULE') {
						this.registrationFacade.schuleSelected(item.kuerzel);
					}
				} else {
					this.selectedKatalogItem = undefined;
				}
			}
		);
	}

	ngOnDestroy() {

		if (this.newsletterAboStateSubscription) {
			this.newsletterAboStateSubscription.unsubscribe();
		}

		if (this.selectedKatalogItemSubscription) {
			this.selectedKatalogItemSubscription.unsubscribe();
		}

		this.initState();

	}

	onNewsletterChanged(isChecked: boolean) {
		this.registrationFacade.setNewsletterAboState(isChecked);
	}

	gotoKatalogsuche() {
		this.router.navigate([]);
	}

	modusLehrerkonto() {
		this.registrationFacade.activateModusLehrerkonto();
	}

	neueSchulsuche() {
		this.schulkatalogFacade.initSchulkatalog('ORT');
		this.registrationFacade.resetSchulsuche();

	}

	privatkontoAnlegen() {
		this.registrationFacade.privatkontoAnlegen(this.newsletterAbonnieren);
	}

	lehrerkontoAnlegen() {
		this.registrationFacade.lehrerkontoAnlegen(this.selectedKatalogItem.kuerzel, this.newsletterAbonnieren);
	}

	cancel() {
		this.router.navigateByUrl('/');
	}

	toggleInfoNewsletter() {
		this.showInfoNewsletter = !this.showInfoNewsletter;
	}

	private initState() {
		this.registrationFacade.resetRegistrationState();
		this.schulkatalogFacade.initSchulkatalog('ORT');
	}

}
