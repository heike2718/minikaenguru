import { Component, OnInit, ViewChild } from '@angular/core';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap';
import { environment } from '../../environments/environment';
import { WettbewerbeFacade } from '../wettbewerbe/wettbewerbe.facade';

@Component({
	selector: 'mkod-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

	collapsed = true;
	logo: string;

	@ViewChild(NgbCollapse, { static: true }) navbarToggler: NgbCollapse;

	constructor(private wettbewerbeFacade: WettbewerbeFacade) { }

	collapseNav() {
		if (this.navbarToggler) {
			this.collapsed = true;
		}
	}

	ngOnInit() {
		this.logo = environment.assetsUrl + '/mja-logo.png';
		this.wettbewerbeFacade.loadWettbewerbe();
	}
}
