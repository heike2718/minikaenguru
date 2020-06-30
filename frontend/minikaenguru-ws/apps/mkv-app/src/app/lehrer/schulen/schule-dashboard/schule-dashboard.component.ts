import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { Router } from '@angular/router';
import { LehrerFacade } from '../../lehrer.facade';
import { WettbewerbFacade } from '../../../wettbewerb/wettbewerb.facade';
import { AuthService, User } from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';
import { Schule } from '../schulen.model';
import { LogService } from '@minikaenguru-ws/common-logging';

@Component({
	selector: 'mkv-schule-dashboard',
	templateUrl: './schule-dashboard.component.html',
	styleUrls: ['./schule-dashboard.component.css']
})
export class SchuleDashboardComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	details$ = this.lehrerFacade.schuleDetails$;

	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;

	loading$ = this.lehrerFacade.loading$;

	private user: User;

	schule: Schule;

	private userSubscription: Subscription;

	private schuleSubscription: Subscription;

	constructor(private router: Router,
		private lehrerFacade: LehrerFacade,
		private authService: AuthService,
		private wettbewerbFacade: WettbewerbFacade,
		private logger: LogService) {
	}

	ngOnInit(): void {

		this.userSubscription = this.authService.user$.subscribe(
			u => this.user = u
		);

		this.schuleSubscription = this.lehrerFacade.selectedSchule$.subscribe(
			s => {
				this.schule = s;
				this.logger.debug(JSON.stringify(this.schule));
			}
		);
	}

	ngOnDestroy(): void {
		if (this.userSubscription) {
			this.userSubscription.unsubscribe();
		}

		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}
	}

	anmelden(): void {
		this.lehrerFacade.schuleAnmelden(this.schule, this.user);
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.schule.kuerzel);
	}

	gotoTeilnahmen(): void {

	}

	backToSchulen(): void {
		this.lehrerFacade.resetSelection();
		this.router.navigateByUrl('/lehrer/schulen');
	}



}
