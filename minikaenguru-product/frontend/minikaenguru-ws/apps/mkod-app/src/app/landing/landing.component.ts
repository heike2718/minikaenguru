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


	constructor(private router: Router, public versionService: VersionService, private logger: LogService) { }

	ngOnInit(): void {
		this.versionService.ladeExpectedGuiVersion();
	}

	ngOnDestroy(): void {
	}

	gotoWettbewerbe(): void {
		this.router.navigateByUrl('/wettbewerbe');
	}
	
	gotoAnmeldungen(): void {
		this.router.navigateByUrl('/anmeldungen');
	}

	

}
