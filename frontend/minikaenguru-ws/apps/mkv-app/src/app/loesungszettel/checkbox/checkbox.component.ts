import { Component, OnInit, Input, EventEmitter, Output, OnDestroy } from '@angular/core';
import { LoesungszettelFacade } from '../loesungszettel.facade';
import { Subscription } from 'rxjs';
import { Loesungszettelzeile, CheckboxData } from '../loesungszettel.model';

@Component({
	selector: 'mkv-checkbox',
	templateUrl: './checkbox.component.html',
	styleUrls: ['./checkbox.component.css']
})
export class CheckboxComponent implements OnInit, OnDestroy {

	@Input()
	rowIndex: number;

	@Input()
	columnIndex: number;

	@Output()
	checkboxClicked: EventEmitter<CheckboxData> = new EventEmitter<CheckboxData>();

	checked: boolean;

	private selectedLoesungszettelSubscription: Subscription;

	constructor(private loesungszettelFacace: LoesungszettelFacade) { }

	ngOnInit(): void {

		this.selectedLoesungszettelSubscription = this.loesungszettelFacace.selectedLoesungszettel$.subscribe(
			zettel => {
				if (zettel) {

					const zeile: Loesungszettelzeile = zettel.zeilen[this.rowIndex];

					switch (zeile.eingabe) {
						case 'A': {
							if (this.columnIndex === 0) {
								this.checked = true;
							} else {
								this.checked = false;
							}
							break;
						}
						case 'B': {
							if (this.columnIndex === 1) {
								this.checked = true;
							} else {
								this.checked = false;
							}
							break;
						}
						case 'C': {
							if (this.columnIndex === 2) {
								this.checked = true;
							} else {
								this.checked = false;
							}
							break;
						}
						case 'D': {
							if (this.columnIndex === 3) {
								this.checked = true;
							} else {
								this.checked = false;
							}
							break;
						}
						case 'E': {
							if (this.columnIndex === 4) {
								this.checked = true;
							} else {
								this.checked = false;
							}
							break;
						}
						default: this.checked = false; break;
					}
				}
			}
		);
	}

	ngOnDestroy(): void {

		if (this.selectedLoesungszettelSubscription) {
			this.selectedLoesungszettelSubscription.unsubscribe();
		}
	}

	onClickEvent(_$event): void {

		this.checked = !this.checked;

		const checkboxData: CheckboxData = {
			rowIndex: this.rowIndex, columnIndex: this.columnIndex, checked: this.checked
		};
		this.checkboxClicked.emit(checkboxData);
	}

}
