import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Session, user } from '@minikaenguru-ws/common-auth';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent {


	user$ = this.sessionStore.select(user);


	constructor(private sessionStore: Store<Session>,
		 private router: Router) { }

	gotoSchulen() {

		console.log('navigate to dashboard');
		this.router.navigateByUrl('/schulen');
	}

	gotoDownloadUnterlagen() {
		console.log('hier gehts zu den Unterlagen: Achtung - vorher Status abfragen, ob angemeldet und freigeschaltet!');
	}

	gotoProfil() {
		console.log('hier gehts zum Profil');
	}
}
