import { Component, OnInit, Input } from '@angular/core';
import { LoesungszettelFacade } from '../loesungszettel.facade';
import { CheckboxData } from '../loesungszettel.model';
import { Loesungszettelzeile, ZulaessigeEingabe } from '@minikaenguru-ws/common-components';

@Component({
	selector: 'mkv-loesungszettelrow',
	templateUrl: './loesungszettelrow.component.html',
	styleUrls: ['./loesungszettelrow.component.css']
})
export class LoesungszettelrowComponent implements OnInit {

	@Input()
	zeile!: Loesungszettelzeile;

	columnIdexes: number[] = [];

	constructor(private loesungszettelFacade: LoesungszettelFacade) { }

	ngOnInit(): void {

		this.columnIdexes = [];
		for (let i = 0; i < this.zeile.anzahlSpalten; i++) {
			this.columnIdexes.push(i);
		}
	}


	onCheckboxClicked($event: any): void {

		const value = $event as CheckboxData;

		if (this.zeile.index === value.rowIndex) {



			if (value.checked) {

				let eingabe: ZulaessigeEingabe = 'N';

				switch (value.columnIndex) {
					case 0: eingabe = 'A'; break;
					case 1: eingabe = 'B'; break;
					case 2: eingabe = 'C'; break;
					case 3: eingabe = 'D'; break;
					case 4: eingabe = 'E'; break;
					default: eingabe = 'N'; break;
				}

				const loesungszettelzeile:Loesungszettelzeile = { ...this.zeile, eingabe: eingabe };
				this.loesungszettelFacade.loesungszettelChanged(loesungszettelzeile);

			} else {
				const loesungszettelzeile: Loesungszettelzeile = { ...this.zeile, eingabe: 'N' };
				this.loesungszettelFacade.loesungszettelChanged(loesungszettelzeile);
			}


		}

	}

	calculateBackgroundColor(): string {

		if (this.zeile.name.startsWith('A')) {
			return '#def7d4';
		}
		if (this.zeile.name.startsWith('B')) {
			return '#ffffb3';
		}
		if (this.zeile.name.startsWith('C')) {
			return '#ffe0b3';
		}

		return 'white';

	}
}
