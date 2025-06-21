import { Component, inject } from "@angular/core";
import { FeedbackFacade } from "../feedback.facade";
import { LehrerFacade } from "../../lehrer/lehrer.facade";
import { Router } from "@angular/router";


@Component({
	selector: 'mkv-feedback-wettbewerb',
	templateUrl: './feedback-wettbewerb-component.html',
	styleUrls: ['./feedback-wettbewerb-component.css']
})
export class FeedbackWettbewerbComponent {

	lehrerFacade = inject(LehrerFacade);
	feedbackFacade = inject(FeedbackFacade);
	#router = inject(Router);

	startBewertungKlasseEins(): void {
		this.feedbackFacade.loadAufgabenvorschau('EINS');
	}

	startBewertungKlasseZwei(): void {
		this.feedbackFacade.loadAufgabenvorschau('ZWEI');
	}

	gotoDashboard(): void {
		this.#router.navigateByUrl('lehrer/dashboard')
	}

}