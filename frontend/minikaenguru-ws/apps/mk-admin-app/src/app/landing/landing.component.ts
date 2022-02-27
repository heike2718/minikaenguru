import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthService, isLoggedOut } from '@minikaenguru-ws/common-auth';
import { Store } from '@ngrx/store';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';
import { Subscription } from 'rxjs';
import { STORAGE_KEY_GUI_VERSION} from '@minikaenguru-ws/common-auth';
import { environment } from '../../environments/environment';
import { AppState } from '../reducers';
import { LogService } from '@minikaenguru-ws/common-logging';

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
    , private versionService: VersionService
    , private logger: LogService) { }

    ngOnInit(): void {
      this.versionSubscription = this.versionService.ladeExpectedGuiVersion().subscribe(
        v => {
  
          const storedGuiVersion = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_GUI_VERSION);
          this.version = v;
  
          if (this.version !== storedGuiVersion) {
            this.versionService.storeGuiVersion(environment.storageKeyPrefix + STORAGE_KEY_GUI_VERSION, this.version);
          } else {
            this.logger.info('GUI-Version ist aktuell');
          }
        }
      );		
    }

  login() {
		this.authService.login();
	}
  
  ngOnDestroy(): void {
		
		this.versionSubscription.unsubscribe();
	}

}
