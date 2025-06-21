import { Component, OnInit } from '@angular/core';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';
import { WettbewerbFacade } from './wettbewerb.facade';

@Component({
	selector: 'mkv-wettbewerb-info',
	templateUrl: './wettbewerb-info.component.html',
	styleUrls: ['./wettbewerb-info.component.css']
})
export class WettbewerbInfoComponent implements OnInit {


	aktuellerWettbewerb$ = this.wettbewerbFacade.aktuellerWettbewerb$;
	isLoggedIn$ = this.authService.isLoggedIn$;


	constructor(private wettbewerbFacade: WettbewerbFacade, private authService: AuthService, private router: Router) { }

	ngOnInit(): void {
	}

	gotoDashboard(): void {
		this.router.navigateByUrl('/dashboard');
	}

}
