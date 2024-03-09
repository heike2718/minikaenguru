import { Injectable, inject } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { FeedbackService } from "../feedback.service";
import * as FeedbackActions from './feedback.actions';
import { map, switchMap, tap } from "rxjs/operators";
import { Router } from "@angular/router";
import { MessageService } from "@minikaenguru-ws/common-messages";


@Injectable()
export class FeedbackEffects {

    #actions = inject(Actions);
    #feedbackService = inject(FeedbackService);
    #router = inject(Router);
    #messageService = inject(MessageService);

    loadAufgabenvorschau$ = createEffect(

        () => this.#actions.pipe(
            ofType(FeedbackActions.loadAufabenvorschau),
            switchMap(action => this.#feedbackService.loadAufgabenvorschau(action.klassenstufe)),
            map(aufgabenvorschau => FeedbackActions.aufgabenvorschauGeladen({ aufgabenvorschau }))
        )
    );

    submitBewertung$ = createEffect(

        () => this.#actions.pipe(
            ofType(FeedbackActions.submitBewertung),
            switchMap(action => this.#feedbackService.saveBewertung(action.bewertungsbogen)),
            map((message) => FeedbackActions.bewertungSubmitted({ message }))
        )
    );

    bewertungSubmitted$ = createEffect(() =>

        this.#actions.pipe(
            ofType(FeedbackActions.bewertungSubmitted),
            tap((message) => {
                this.#messageService.showMessage(message.message)
            }),
        ), { dispatch: false });



    navigateToBewertungsbogen$ = createEffect(() =>

        this.#actions.pipe(
            ofType(FeedbackActions.aufgabenvorschauGeladen),
            tap(() => {
                this.#router.navigateByUrl('feedback/klasse');
            }),
        ), { dispatch: false });
}
