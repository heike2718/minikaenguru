import { Component, inject } from "@angular/core";
import { LehrerFacade } from "../lehrer.facade";


@Component({
	selector: 'mkv-bewertung',
	templateUrl: './bewertungsfragebogen.component.html',
	styleUrls: ['./bewertungsfragebogen.component.css']
})
export class BewertungsfragebogenComponent {

	lehrerFacade = inject(LehrerFacade);

}