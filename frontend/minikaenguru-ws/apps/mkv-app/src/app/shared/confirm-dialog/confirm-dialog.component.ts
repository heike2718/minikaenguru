import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
	selector: 'mkv-confirm-dialog',
	templateUrl: './confirm-dialog.component.html',
	styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent implements OnInit {

	@Input()
	titel: string;

	@Input()
	question: string;

	@Input()
	okLabel: string;

	@Input()
	cancelLabel: string;

	@Output()
	decisionMade = new EventEmitter<string>();

	constructor(private modal: NgbActiveModal) {}

	ngOnInit(): void {
	}

	cancel(): void {

		this.modal.dismiss('Cross click');
		this.decisionMade.emit('cancel');

	}

	save(): void {
		this.modal.dismiss('ok click');
		this.decisionMade.emit('ok');
	}

}
