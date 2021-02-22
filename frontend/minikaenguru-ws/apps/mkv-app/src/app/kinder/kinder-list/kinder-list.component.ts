import { Component, OnInit, OnDestroy, HostListener } from '@angular/core';
import { environment } from '../../../environments/environment';
import { KinderFacade } from '../kinder.facade';
import { PrivatveranstalterFacade } from '../../privatveranstalter/privatveranstalter.facade';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { STORAGE_KEY_USER, User, Rolle } from '@minikaenguru-ws/common-auth';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';
import { KlassenFacade } from '../../klassen/klassen.facade';
import { ThrowStmt } from '@angular/compiler';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { MessageService } from '@minikaenguru-ws/common-messages';


@Component({
	selector: 'mkv-kinder-list',
	templateUrl: './kinder-list.component.html',
	styleUrls: ['./kinder-list.component.css']
})
export class KinderListComponent implements OnInit, OnDestroy {


	devMode = environment.envName === 'DEV';

	selectedKlasse$ = this.klassenFacade.selectedKlasse$;

	kinder$ = this.kinderFacade.kinder$;
	anzahlKinder$ = this.kinderFacade.anzahlKinder$;

	veranstalter$ = this.wettbewerbFacade.veranstalter$;

	labelBtnCancel = 'Übersicht';

	private teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb;

	private veranstalterSubscription: Subscription;

	private teilnahmeIdentifierSubscription: Subscription;

	private klasseSubscription: Subscription;

	private klasseUuid: string;

	constructor(private kinderFacade: KinderFacade,
		private klassenFacade: KlassenFacade,
		private privatveranstalterFacade: PrivatveranstalterFacade,
		private lehrerFacade: LehrerFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private messageService: MessageService,
		private router: Router) { }

	ngOnInit(): void {

		this.veranstalterSubscription = this.veranstalter$.subscribe(
			v => {

				const user: User = this.readUser();

				if (user && v === undefined) {
					switch (user.rolle) {
						case 'LEHRER':
							this.lehrerFacade.loadLehrer();
							if (this.klasseUuid === undefined) {
								this.gotoDashboard();
							}
							break;
						case 'PRIVAT':
							this.privatveranstalterFacade.loadInitialTeilnahmeinfos();
							break;
						default: break;

					}
				}
			}
		);

		this.teilnahmeIdentifierSubscription = this.kinderFacade.teilnahmeIdentifier$.subscribe(
			ti => {
				this.teilnahmeIdentifier = ti;

				if (ti && ti.teilnahmeart === 'SCHULE') {
					this.labelBtnCancel = 'Klassen';
				} else {
					this.labelBtnCancel = 'Übersicht';
				}
			}
		);

		this.klasseSubscription = this.selectedKlasse$.subscribe(
			kl => {
				if (kl) {
					this.klasseUuid = kl.uuid;
				}
			}
		);
	}

	ngOnDestroy(): void {
		if (this.veranstalterSubscription) {
			this.veranstalterSubscription.unsubscribe();
		}
		if (this.teilnahmeIdentifierSubscription) {
			this.teilnahmeIdentifierSubscription.unsubscribe();
		}
		if (this.klasseSubscription) {
			this.klasseSubscription.unsubscribe();
		}
	}

	addKind(): void {
		this.kinderFacade.createNewKind(this.klasseUuid);

		const url = '/kinder/kind-editor/neu';

		if (this.klasseUuid) {
			this.router.navigate([url], { queryParams: { klasseUuid: this.klasseUuid } });
		} else {
			this.router.navigateByUrl(url);
		}
	}

	gotoDashboard(): void {

		this.messageService.clear();

		const user = this.readUser();

		if (user) {
			if (user.rolle === 'PRIVAT') {
				this.router.navigateByUrl('/privat/dashboard');
			}
			if (user.rolle === 'LEHRER') {

				if (!this.teilnahmeIdentifier) {
					this.router.navigateByUrl('/lehrer/dashboard');
				} else {
					this.router.navigateByUrl('/klassen/' + this.teilnahmeIdentifier.teilnahmenummer);
				}
			}
		} else {
			this.router.navigateByUrl('/dashboard');
		}
	}

	private readUser(): User {
		const obj = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);
		if (obj) {
			return JSON.parse(obj);
		}
		return undefined;
	}
}
