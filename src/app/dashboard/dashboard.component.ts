import { Component, OnInit } from '@angular/core';
import { environment } from 'src/environments/environment';

@Component({
	selector: 'mkadm-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

	pathLogo = environment.assetsUrl + '/mja_logo.png';

	constructor() { }

	ngOnInit() {
	}

}
