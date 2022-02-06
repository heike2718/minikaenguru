import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LogService } from '@minikaenguru-ws/common-logging';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';
import { STORAGE_KEY_GUI_VERSION} from '@minikaenguru-ws/common-auth';
import { Subscription } from 'rxjs';
import { environment } from '../../environments/environment';

@Component({
	selector: 'mkod-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {

	version = '';

	private versionSubscription: Subscription = new Subscription();

	constructor(private router: Router, private versionService: VersionService, private logger: LogService) { }

	ngOnInit(): void {
		this.versionSubscription = this.versionService.ladeExpectedGuiVersion().subscribe(
			v => {

				const storedGuiVersion = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_GUI_VERSION);
				this.version = v;

				if (this.version !== storedGuiVersion) {
					this.versionService.storeGuiVersionAndReloadApp(environment.storageKeyPrefix + STORAGE_KEY_GUI_VERSION, this.version);
				} else {
					this.logger.info('GUI-Version ist aktuell');
				}
			}
		);
	}

	ngOnDestroy(): void {
		this.versionSubscription.unsubscribe();
	}

	gotoWettbewerbe(): void {
		this.router.navigateByUrl('/wettbewerbe');
	}
	
	gotoAnmeldungen(): void {
		this.router.navigateByUrl('/anmeldungen');
	}

}
