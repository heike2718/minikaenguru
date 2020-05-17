import { Component, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Session, isLoggedIn } from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';

@Component({
	selector: 'mkv-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {


	private loggedInSubscription: Subscription;

	constructor(private store: Store<Session>, private router: Router) { }

	ngOnInit() {

		this.loggedInSubscription = this.store.select(isLoggedIn).subscribe(
			val => {
				if (val) {
					// this.router.navigateByUrl('dashboard');
					// machen mal nüscht
				}
			}
		);

	}

	ngOnDestroy() {

		if (this.loggedInSubscription) {
			this.loggedInSubscription.unsubscribe();
		}
	}

}
