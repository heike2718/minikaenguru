import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Session, user } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';

@Component({
	selector: 'mka-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

	user$ = this.sessionStore.select(user);

	constructor(private sessionStore: Store<Session>, private router: Router) { }

	ngOnInit(): void {
	}

	gotoAktuelleMeldung() {
		this.router.navigateByUrl('/aktuelle-meldung');
	}

	gotoWettbewerbe() {
		this.router.navigateByUrl('/wettbewerbe');
	}

	gotoVeranstalter() {
		this.router.navigateByUrl('/veranstalter');
	}

	gotoKataloge() {
		this.router.navigateByUrl('/katalogpflege');
	}

	gotoEventlog() {
		this.router.navigateByUrl('/eventlog');
	}

	gotoNewsletters() {
		this.router.navigateByUrl('/newsletters');
	}
}
