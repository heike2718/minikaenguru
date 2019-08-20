import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';

@Component({
	selector: 'mkadm-about',
	templateUrl: './about.component.html',
	styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

	version = environment.version;

	pathLogo = environment.assetsUrl + '/mja_logo.png';

	envName = environment.envName;

	apiUrl = environment.apiUrl;

	constructor() { }

	ngOnInit() {
	}

}
