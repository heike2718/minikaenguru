import { Component, inject } from "@angular/core";
import { FeedbackFacade } from "../feedback.facade";
import { environment } from "apps/mkv-app/src/environments/environment";

@Component({
	selector: 'mkv-bewertungsbogen',
	templateUrl: './bewertungsbogen.component.html',
	styleUrls: ['./bewertungsbogen.component.css']
})
export class BewertungsbogenComponent {

	devMode = environment.envName === 'DEV';

	feedbackFacade = inject(FeedbackFacade);
}