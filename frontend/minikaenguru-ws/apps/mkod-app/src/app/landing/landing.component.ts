import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { VersionService } from 'libs/common-components/src/lib/version/version.service';
import { Subscription } from 'rxjs';

@Component({
	selector: 'mkod-landing',
	templateUrl: './landing.component.html',
	styleUrls: ['./landing.component.css']
})
export class LandingComponent implements OnInit, OnDestroy {

	version = '';

	private versionSubscription: Subscription = new Subscription();

	constructor(private router: Router, private versionService: VersionService) { }

	ngOnInit(): void {
		this.versionSubscription = this.versionService.ladeExpectedGuiVersion().subscribe(
			v => this.version = v
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
