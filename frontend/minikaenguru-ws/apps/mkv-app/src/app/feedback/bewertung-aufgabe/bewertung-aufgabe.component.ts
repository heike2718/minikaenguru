import { Component, Input } from "@angular/core";
import { Aufgabe, BewertungAufgabe } from "../feedback.model";


@Component({
	selector: 'mkv-bewertung-aufgabe',
	templateUrl: './bewertung-aufgabe.component.html',
	styleUrls: ['./bewertung-aufgabe.component.css']
})
export class BewertungAufgabeComponent {

    @Input()
    vorschau!: Aufgabe;

    @Input()
    bewertungAufgabe!: BewertungAufgabe;
    
}