import { Component, OnInit, Input, TemplateRef } from '@angular/core';
import { EventManager } from '@angular/platform-browser';
import { DialogService } from './dialog.service';

@Component({
	selector: 'mk-dialog',
	templateUrl: './dialog.component.html',
	styleUrls: ['./dialog.component.css']
})
export class DialogComponent implements OnInit {


	@Input()
	body: TemplateRef<any>;

	@Input()
	context: any;

	@Input()
	hideOnEscape = true;

	@Input()
	hideOnClickOutside = true;

	constructor(private dialogService: DialogService,
		private eventManager: EventManager) { }

	ngOnInit() {

		// if (this.context && this.context['closeImmediate'] && this.context['closeImmediate'] === true) {
		//   this.close();
		// }

		this.eventManager.addGlobalEventListener('window', 'keyup.esc', () => {
			if (this.hideOnEscape) {
				this.close();
			}
		});
	}

	close() {
		this.dialogService.close();
	}

	onClickOutsideModal() {
		if (this.hideOnClickOutside) {
			this.close();
		}
	}

	cancelClick(event: KeyboardEvent) {
		event.preventDefault();
		event.stopPropagation();
	}
}


