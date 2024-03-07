import { Injectable, inject } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { FeedbackService } from "../feedback.service";
import * as FeedbackActions from './feedback.actions';
import { map, switchMap, tap } from "rxjs/operators";
import { Router } from "@angular/router";


@Injectable()
export class FeedbackEffects {

    #actions = inject(Actions);
    #feedbackService = inject(FeedbackService);
    #router = inject(Router);

   loadAufgabenvorschau$ = createEffect(

		() => this.#actions.pipe(
			ofType(FeedbackActions.loadAufabenvorschau),
			switchMap(action => this.#feedbackService.loadAufgabenvorschau(action.klassenstufe)),
			map(aufgabenvorschau => FeedbackActions.aufgabenvorschauGeladen({ aufgabenvorschau }))
		)
	);

    navigateToBewertungsbogen$ = createEffect(() =>

        this.#actions.pipe(
            ofType(FeedbackActions.aufgabenvorschauGeladen),
            tap(() => {
                this.#router.navigateByUrl('feedback/klasse');
            }),
        ), { dispatch: false });
}
