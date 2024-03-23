import { Component, OnInit, Testability } from '@angular/core';
import { Irgendwas } from '../layouttests.model';

@Component({
	selector: 'mka-irgendwas-list',
	templateUrl: './irgendwas-list.component.html',
	styleUrls: ['./irgendwas-list.component.css']
})
export class IrgendwasListComponent implements OnInit {

	irgendwasListe: Irgendwas[] = [];

	constructor() { }

	ngOnInit(): void {

		this.irgendwasListe.push({
			name: 'Erstes Teil',
			beschreibung: 'Ggigis giwgiegi gwuiwg dguw'
		});
		this.irgendwasListe.push({
			name: 'Zweites Teil',
			beschreibung: 'Hguaidg gwuiwg dguw'
		});
		this.irgendwasListe.push({
			name: 'Drittes Teil',
			beschreibung: 'Zgsh wgil dwgui Zail'
		});
		this.irgendwasListe.push({
			name: 'Viertes Teil',
			beschreibung: 'Agwfk htewu Eszdwt'
		});
		this.irgendwasListe.push({
			name: 'Drittes Teil',
			beschreibung: 'Uhaj qqqjiow Lsjod Uwhd'
		});
	}

}
