import { Component, OnInit, OnDestroy } from '@angular/core';
import { Store } from '@ngrx/store';
import { Session, User, selectIsLoggedIn, selectUser } from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkv-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {


	isLoggedIn$ = this.store.select(selectIsLoggedIn);

	user: User;

	private userSubscription: Subscription;

	constructor(private store: Store<Session>) { }

	ngOnInit() {

		this.userSubscription = this.store.select(selectUser).subscribe(
			u => this.user = u
		);

	}

	ngOnDestroy() {

		if (this.userSubscription) {
			this.userSubscription.unsubscribe();
		}
	}

}
