import { Component, Input } from "@angular/core";
import { Aufgabe } from "../feedback.model";


@Component({
	selector: 'mkv-feedback-aufgabenvorschau',
	templateUrl: './aufgabenvorschau.component.html',
	styleUrls: ['./aufgabenvorschau.component.css']
})
export class AufgabenvorschauComponent {

    @Input()
    aufgabe!: Aufgabe;
}