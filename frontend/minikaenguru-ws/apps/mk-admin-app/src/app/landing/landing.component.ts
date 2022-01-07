import { Component, OnInit } from '@angular/core';
import { AuthService, isLoggedOut } from '@minikaenguru-ws/common-auth';
import { Store } from '@ngrx/store';
import { environment } from '../../environments/environment';
import { AppState } from '../reducers';

@Component({
  selector: 'mka-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit {

  isLoggedOut$ = this.authStore.select(isLoggedOut);
  

  constructor(private authStore: Store<AppState>
    , private authService: AuthService) { }

  ngOnInit(): void {
  }

  login() {
		this.authService.login();
	}

}
