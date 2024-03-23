import { Component, Input } from "@angular/core";


@Component({
	selector: 'mkv-feedback-aufgabenvorschau',
	templateUrl: './aufgabenvorschau.component.html',
	styleUrls: ['./aufgabenvorschau.component.css']
})
export class AufgabenvorschauComponent {

	@Input()
    nummer!: string;

	@Input()
    imageBase64!: string;

	@Input()
	style!: Object;
}