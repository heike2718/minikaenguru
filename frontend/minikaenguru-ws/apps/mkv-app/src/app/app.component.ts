import { Component } from '@angular/core';
import { environment } from '../environments/environment';

@Component({
	selector: 'mkv-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.css']
})
export class AppComponent {

	title = 'Minikänguru Onlineauswertung';

	envName = environment.envName;
	showEnv = !environment.production;
	api = environment.apiUrl;
	version = environment.version;

	constructor() {
		const STORAGE_KEY_ID_REFERENCE = 'mkv-app-id-reference';
		sessionStorage.setItem(STORAGE_KEY_ID_REFERENCE, 'anonym');
	}

}
