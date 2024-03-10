import { Component, OnInit, Input, OnDestroy, inject } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';
import { User, AuthService } from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';
import { Wettbewerb } from '../../../wettbewerb/wettbewerb.model';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	textAuswertungsmodus = 'noch nicht entschieden';

	#logService = inject(LogService);

	@Input()
	schule!: Schule;

	wettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	showBtnOnlineauswertung = false;
	showBtnUploadAuswertung = false;

	#wettbewerb: Wettbewerb | undefined;
	#wettbewerbSubscription = new Subscription();



	private user!: User | null;

	private userSubscription: Subscription = new Subscription();

	constructor(private router: Router,
		private lehrerFacade: LehrerFacade,
		private wettbewerbFacade: WettbewerbFacade,
		private authService: AuthService) { }

	ngOnInit(): void {

		this.userSubscription = this.authService.user$.subscribe(
			u => this.user = u
		);

		switch (this.schule.auswertungsmodus) {
			case 'OFFLINE': this.textAuswertungsmodus = 'OFFLINE (Sie erstellen die Auswertung und die Urkunden selbst)'; break;
			case 'ONLINE': this.textAuswertungsmodus = 'ONLINE'; break;
			default: break;
		}

		this.#wettbewerbSubscription = this.wettbewerb$.subscribe(
			w => {
				if (w) {
					this.#wettbewerb = w;
				}
			}
		)

		this.showBtnOnlineauswertung = this.schule.aktuellAngemeldet && this.schule.auswertungsmodus !== 'OFFLINE';
		this.showBtnUploadAuswertung = this.schule.aktuellAngemeldet && this.schule.auswertungsmodus !== 'ONLINE';
	}

	ngOnDestroy(): void {
		this.userSubscription.unsubscribe();
		this.#wettbewerbSubscription.unsubscribe();
	}

	showBtnAnmelden(): boolean {

		if (!this.#wettbewerb) {
			this.#logService.debug('this.#wettbewerb is undefined');
			return false;
		}
		if (this.schule.aktuellAngemeldet) {
			this.#logService.debug('this.schule is angemeldet');
			return false;
		}

		this.#logService.debug('this.schule is not angemeldet');

		const result = this.#wettbewerb.status === 'ANMELDUNG' || this.#wettbewerb.status === 'DOWNLOAD_LEHRER' || this.#wettbewerb.status === 'DOWNLOAD_PRIVAT';
		this.#logService.debug('result=' + result + ', #wettbewerb.status=' + this.#wettbewerb.status);
		return result;

	}

	schuleAnmelden(): void {

		if (this.user === null) {
			return;
		}
		this.lehrerFacade.selectSchule(this.schule);
		this.lehrerFacade.schuleAnmelden(this.schule, this.user);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

	selectSchule(): void {
		this.lehrerFacade.selectSchule(this.schule);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

	gotoAuswertung(): void {
		this.lehrerFacade.selectSchule(this.schule);
		this.router.navigateByUrl('/klassen/' + this.schule.kuerzel);
	}

	gotoUploadAuswertung(): void {
		this.lehrerFacade.selectSchule(this.schule);
		this.router.navigateByUrl('lehrer/upload-auswertung');
	}

	vonSchuleAbmelden(): void {
		this.lehrerFacade.removeSchule(this.schule);
	}
}
