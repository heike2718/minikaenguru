import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LogoutService } from '../services/logout.service';

@Component({
  selector: 'mka-session-timeout',
  templateUrl: './session-timeout.component.html',
  styleUrls: ['./session-timeout.component.css']
})
export class SessionTimeoutComponent implements OnInit {

	constructor(private router: Router, private logoutService: LogoutService) { }

	ngOnInit() {
		this.logoutService.logout();
	}

	gotoLandingPage() {
		this.router.navigateByUrl('/landing');
	}

}
