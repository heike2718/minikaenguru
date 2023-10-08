import { Injectable, inject } from "@angular/core";
import { AdminSchulkatalogHttpService } from "../admin-schulkatalog-http.service";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import * as KatalogActions from './admin-katalog.actions';
import { map, switchMap, tap } from "rxjs/operators";
import { Router } from "@angular/router";
import { dispatch } from "rxjs/internal/observable/pairs";


@Injectable({
    providedIn: 'root'
})
export class AdminSchulkatalogEffects {


    #router = inject(Router);
    #schulkatalogHttpService = inject(AdminSchulkatalogHttpService);
    #actions$ = inject(Actions);

    loadLaender$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.loadLaender),
            switchMap(_action => this.#schulkatalogHttpService.loadLaender()),
            map((laender) => KatalogActions.laenderGeladen({ laender }))
        )
    );

    landSelected$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.landSelected),
            tap(() => this.#router.navigateByUrl('schulkatalog/land'))
        ), { dispatch: false });

    laenderGeladen$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.laenderGeladen),
            tap(() => this.#router.navigateByUrl('schulkatalog/laender'))
        ), { dispatch: false });

    loadOrte$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.loadOrte),
            switchMap(action => this.#schulkatalogHttpService.loadOrte(action.land)),
            map((sucheResult) => KatalogActions.orteGeladen({land: sucheResult.land, orte: sucheResult.orte}))
        )
    );

    ortSelected$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.ortSelected),
            tap(() => this.#router.navigateByUrl('schulkatalog/ort'))
        ), { dispatch: false });

    loadSchulen$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.loadSchulen),
            switchMap(action => this.#schulkatalogHttpService.loadSchulen(action.ort)),
            map((sucheResult) => KatalogActions.schulenGeladen({ort: sucheResult.ort, schulen: sucheResult.schulen}))
        )
    );

    schuleSelected$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.schuleSelected),
            tap(() => this.#router.navigateByUrl('schulkatalog/schule'))
        ), { dispatch: false });
}