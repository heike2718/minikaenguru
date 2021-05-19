import { Component } from '@angular/core';
import { environment } from '../environments/environment';
import { RouterOutlet } from '@angular/router';

@Component({
	selector: 'mkod-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {

	envName = environment.envName;
	showEnv = this.envName === 'DEV';
	api = environment.apiUrl;
	version = environment.version;

	constructor() {
		// const hash = window.location.hash;

	}

	getAnimationData(outlet: RouterOutlet) {
		return outlet && outlet.activatedRouteData && outlet.activatedRouteData['animation'];
	}

}
