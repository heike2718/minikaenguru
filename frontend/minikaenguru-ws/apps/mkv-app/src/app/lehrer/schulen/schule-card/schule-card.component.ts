import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Schule } from '../schulen.model';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';
import { User, AuthService } from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkv-schule-card',
	templateUrl: './schule-card.component.html',
	styleUrls: ['./schule-card.component.css']
})
export class SchuleCardComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	@Input()
	schule!: Schule;

	wettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

    showBtnOnlineauswertung = false;
	showBtnUploadAuswertung = false;

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

		this.showBtnOnlineauswertung = this.schule.aktuellAngemeldet && this.schule.auswertungsmodus !== 'OFFLINE';
		this.showBtnUploadAuswertung = this.schule.aktuellAngemeldet && this.schule.auswertungsmodus !== 'ONLINE';
	}

	ngOnDestroy(): void {
		this.userSubscription.unsubscribe();
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
