import { Component, OnInit } from '@angular/core';
import { TeilnahmenFacade } from '../teilnahmen.facade';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-wettbewerb-info',
	templateUrl: './wettbewerb-info.component.html',
	styleUrls: ['./wettbewerb-info.component.css']
})
export class WettbewerbInfoComponent implements OnInit {


	aktuellerWettbewerb$ = this.teilnahmenFacade.aktuellerWettbewerb$;
	isLoggedIn$ = this.authService.isLoggedIn$;


	constructor(private teilnahmenFacade: TeilnahmenFacade, private authService: AuthService, private router: Router) { }

	ngOnInit(): void {
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
