import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../environments/environment';
import { KinderFacade } from '../kinder.facade';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { STORAGE_KEY_USER, User } from '@minikaenguru-ws/common-auth';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { TeilnahmeIdentifier } from '@minikaenguru-ws/common-components';


@Component({
	selector: 'mkv-kinder-list',
	templateUrl: './kinder-list.component.html',
	styleUrls: ['./kinder-list.component.css']
})
export class KinderListComponent implements OnInit, OnDestroy {


	devMode = !environment.production;

	kinder$ = this.kinderFacade.kinder$;
	anzahlKinder$ = this.kinderFacade.anzahlKinder$;

	veranstalter$ = this.privatveranstalterFacade.veranstalter$;

	private teilnahmeIdentifier: TeilnahmeIdentifier;

	private veranstalterSubscription: Subscription;

	private teilnahmeIdentifierSubscription: Subscription;

	constructor(private kinderFacade: KinderFacade,
		private privatveranstalterFacade: PrivatveranstalterFacade,
		private lehrerFacade: LehrerFacade,
		private router: Router) { }

	ngOnInit(): void {

		this.veranstalterSubscription = this.veranstalter$.subscribe(
			v => {
				if (v === undefined) {
					if (this.teilnahmeIdentifier) {
						if (this.teilnahmeIdentifier.teilnahmeart === 'PRIVAT') {
							this.privatveranstalterFacade.loadInitialTeilnahmeinfos();
						}
					}
				}
			}
		);

		this.teilnahmeIdentifierSubscription = this.kinderFacade.teilnahmeIdentifier$.subscribe(
			ti => this.teilnahmeIdentifier = ti
		);
	}

	ngOnDestroy(): void {
		if (this.veranstalterSubscription) {
			this.veranstalterSubscription.unsubscribe();
		}
		if (this.teilnahmeIdentifierSubscription) {
			this.teilnahmeIdentifierSubscription.unsubscribe();
		}
	}


	addKind(): void {
		this.kinderFacade.createNewKind();
		this.router.navigateByUrl('/kinder/kind-editor/neu');
	}

	gotoDashboard(): void {

		const obj = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);

		if (obj) {
			const user: User = JSON.parse(obj);

			if (user.rolle === 'PRIVAT') {
				this.router.navigateByUrl('/privat/dashboard');
			}
			if (user.rolle === 'LEHRER') {
				if (this.teilnahmeIdentifier.teilnahmenummer) {
					this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.teilnahmeIdentifier.teilnahmenummer)
				} else {
					this.router.navigateByUrl('/lehrer/dashboard');
				}
			}
		}
	}

}
