import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment.prod';

@Component({
	selector: 'mkadm-about',
	templateUrl: './about.component.html',
	styleUrls: ['./about.component.scss']
})
export class AboutComponent implements OnInit {

	pathLogo = environment.assetsUrl + '/mja_logo.png';

	constructor() { }

	ngOnInit() {
	}

}
