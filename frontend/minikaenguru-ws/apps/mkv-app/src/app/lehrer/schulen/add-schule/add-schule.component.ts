import { Component, OnInit, OnDestroy } from '@angular/core';
import { LehrerFacade } from '../../lehrer.facade';
import { Router } from '@angular/router';
import { environment } from '../../../../environments/environment';
import { KatalogItem, SchulkatalogFacade } from '@minikaenguru-ws/common-schulkatalog';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkv-add-schule',
	templateUrl: './add-schule.component.html',
	styleUrls: ['./add-schule.component.css']
})
export class AddSchuleComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';
	neueSchule: KatalogItem;

	textSchuleBereitsZugeordnet = 'Sie sind bereits in dieser Schule eingetragen.';

	private selectedKatalogItemSubscription: Subscription;

	constructor(public lehrerFacade: LehrerFacade
		, public schulkatalogFacade: SchulkatalogFacade
		, private router: Router) {

	}

	ngOnInit(): void {

		this.selectedKatalogItemSubscription = this.schulkatalogFacade.selectedKatalogItem$.subscribe(
			item => {
				if (item) {
					this.neueSchule = item;
					if (item.typ === 'SCHULE') {
						this.lehrerFacade.neueSchuleSelected(item);
					}
				} else {
					this.neueSchule = undefined;
				}
			}
		);
	}

	ngOnDestroy(): void {
		if (this.selectedKatalogItemSubscription) {
			this.selectedKatalogItemSubscription.unsubscribe();
		}
	}

	schuleHinzufuegen(): void {
		this.lehrerFacade.schuleHinzufuegen(this.neueSchule);
	}

	resetSuche(): void {
		this.lehrerFacade.neueSchulsuche();
	}

	gotoDashboard(): void {
		this.lehrerFacade.closeSchulsuche();
		this.router.navigateByUrl('/lehrer/dashboard');
	}

	gotoSchulenliste(): void {
		this.lehrerFacade.closeSchulsuche();
		this.router.navigateByUrl('/lehrer/schulen');
	}

}
