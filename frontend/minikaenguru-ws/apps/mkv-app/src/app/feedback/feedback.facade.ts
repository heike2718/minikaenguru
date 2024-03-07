import { Injectable, inject } from "@angular/core";
import { Klassenstufenart } from "@minikaenguru-ws/common-components";
import * as FeedbackActions from './+state/feedback.actions';
import * as FeedbackState from './+state/feedback.selectors';
import { Store } from "@ngrx/store";
import { Observable } from "rxjs";
import { Aufgabenvorschau, BewertungsbogenGUIModel, BewertungsbogenKlassenstufe } from "./feedback.model";
import { filterDefined } from "@minikaenguru-ws/shared/util-mk";

@Injectable({
    providedIn: 'root'
})
export class FeedbackFacade {


    #store = inject(Store);

    bewertungsbogenCreated$: Observable<boolean> = this.#store.select(FeedbackState.bewertungsbogenCreated$);
    bewertungsformularModel$: Observable<BewertungsbogenGUIModel> = this.#store.select(FeedbackState.guiModel$).pipe(filterDefined);

    loadAufgabenvorschau(klassenstufe: Klassenstufenart): void {

        this.#store.dispatch(FeedbackActions.loadAufabenvorschau({ klassenstufe }));

    }

    resetState(): void {
        this.#store.dispatch(FeedbackActions.resetState());
    }

}