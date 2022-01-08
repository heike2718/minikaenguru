import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService, isLoggedOut } from '@minikaenguru-ws/common-auth';
import { Store } from '@ngrx/store';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';
import { Subscription } from 'rxjs';
import { environment } from '../../environments/environment';
import { AppState } from '../reducers';

@Component({
  selector: 'mka-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {

  isLoggedOut$ = this.authStore.select(isLoggedOut);

  version = '';

  private versionSubscription: Subscription = new Subscription();
  

  constructor(private authStore: Store<AppState>
    , private authService: AuthService
    , private versionService: VersionService) { }

    ngOnInit(): void {
      this.versionSubscription = this.versionService.ladeExpectedGuiVersion().subscribe(
        v => this.version = v
      );		
    }

  login() {
		this.authService.login();
	}
  
  ngOnDestroy(): void {
		
		this.versionSubscription.unsubscribe();
	}

}
