import { Injectable, inject } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { MkbizaAPIHttpService } from "../mkbiza-api-http.service";
import { domainActions } from "./domain.actions";
import { map, switchMap } from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class DomainEffects {

    #actions = inject(Actions);
    #httpService = inject(MkbizaAPIHttpService);

    loadWettbewerbe$ = createEffect(() => {

        return this.#actions.pipe(
            ofType(domainActions.lOAD_WETTBEWERBE),
            switchMap(() => this.#httpService.loadWettbewerbe()),
            map((wettbewerbe) => domainActions.wETTBEWERBE_LOADED({ wettbewerbe }))
        );
    });

    loadWettbewerb$ = createEffect(() => {

        return this.#actions.pipe(
            ofType(domainActions.lOAD_WETTBEWERB),
            switchMap((action) => this.#httpService.loadWettbewerb(action.jahr)),
            map((wettbewerb) => domainActions.wETTBEWERB_LOADED({ wettbewerb }))
        );
    });
};