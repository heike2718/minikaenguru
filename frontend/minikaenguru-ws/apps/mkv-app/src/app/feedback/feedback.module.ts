import { CommonModule } from "@angular/common";
import { NgModule } from "@angular/core";
import { CommonLoggingModule } from "@minikaenguru-ws/common-logging";
import { FeedbackWettbewerbComponent } from "./feedback-wettbewerb/feedback-wettbewerb-component";
import { BewertungsbogenComponent } from "./bewertungsbogen/bewertungsbogen.component";
import { BewertungAufgabeComponent } from "./bewertung-aufgabe/bewertung-aufgabe.component";
import { AufgabenvorschauComponent } from "./aufgabenvorschau/aufgabenvorschau.component";

import * as fromFeedback from './+state/feedback.reducer';
import { StoreModule } from "@ngrx/store";
import { FeedbackRoutingModule } from "./feedback-routing.module";
import { EffectsModule } from "@ngrx/effects";
import { FeedbackEffects } from "./+state/feedback.effects";


@NgModule({
	imports: [
		CommonModule,
		CommonLoggingModule,
		FeedbackRoutingModule,
		StoreModule.forFeature(fromFeedback.feedbackFeatureKey, fromFeedback.reducer),
		EffectsModule.forFeature(FeedbackEffects)
	],
	declarations: [
		FeedbackWettbewerbComponent,
		BewertungsbogenComponent,
		BewertungAufgabeComponent,
		AufgabenvorschauComponent
	],
	exports: [
		FeedbackWettbewerbComponent,
	],
	providers: [
	]
})
export class FeedbackModule {}