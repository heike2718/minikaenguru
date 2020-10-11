import { Component, OnInit, OnDestroy } from '@angular/core';
import { environment } from '../../../environments/environment';
import { TeilnahmenFacade } from '../../teilnahmen/teilnahmen.facade';
import { Subscription } from 'rxjs';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-teilnahmen-list',
	templateUrl: './teilnahmen-list.component.html',
	styleUrls: ['./teilnahmen-list.component.css']
})
export class TeilnahmenListComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	teilnahmen$ = this.teilnahmenFacade.anonymisierteTeilnahmen$;

	user$ = this.authService.user$;

	statistikUrlPrefix  = environment.apiUrl + '/statistik/';

	teilnahmenummer: string;

	name: string;

	private teilnahmeIdSubscription: Subscription;

	constructor(private teilnahmenFacade: TeilnahmenFacade, private authService: AuthService, private router: Router) {

		this.teilnahmenummer = 'unbekannt';
	}

	ngOnInit(): void {

		this.teilnahmeIdSubscription = this.teilnahmenFacade.teilnahmenummerAndName$.subscribe(
			tun => {
				if (tun) {
					this.teilnahmenummer = tun.teilnahmenummer;
					this.name = tun.nameSchule;
				}
			}
		);
	}

	ngOnDestroy(): void {
		if (this.teilnahmeIdSubscription) {
			this.teilnahmeIdSubscription.unsubscribe();
		}
	}

	backToSchule(): void {
		this.router.navigateByUrl('/lehrer/schule-dashboard/' + this.teilnahmenummer);
	}

	backToLehrerDashboard(): void {
		this.router.navigateByUrl('/lehrer/dashboard');
	}

	backToPrivatDashboard(): void {
		this.router.navigateByUrl('/privat/dashboard');
	}
}
