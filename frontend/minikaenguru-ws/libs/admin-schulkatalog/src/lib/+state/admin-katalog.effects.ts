import { Injectable, inject } from "@angular/core";
import { AdminSchulkatalogHttpService } from "../admin-schulkatalog-http.service";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import * as KatalogActions from './admin-katalog.actions';
import { map, switchMap, tap } from "rxjs/operators";
import { Router } from "@angular/router";
import { MessageService } from "@minikaenguru-ws/common-messages";


@Injectable({
    providedIn: 'root'
})
export class AdminSchulkatalogEffects {


    #router = inject(Router);
    #schulkatalogHttpService = inject(AdminSchulkatalogHttpService);
    #messageService = inject(MessageService);
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
            map((sucheResult) => KatalogActions.orteGeladen({ land: sucheResult.land, orte: sucheResult.orte }))
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
            map((sucheResult) => KatalogActions.schulenGeladen({ ort: sucheResult.ort, schulen: sucheResult.schulen }))
        )
    );

    schuleSelected$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.schuleSelected),
            tap(() => this.#router.navigateByUrl('schulkatalog/schule'))
        ), { dispatch: false });


    createKuerzel$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.createKuerzel),
            switchMap(() => this.#schulkatalogHttpService.getKuerzel()),
            map((kuerzel) => KatalogActions.kuerzelCreated({ kuerzel }))
        )
    );

    findOrte$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.findOrte),
            switchMap(action => this.#schulkatalogHttpService.findOrte(action.land, action.suchstring)),
            map((sucheResult) => KatalogActions.orteGeladen({ land: sucheResult.land, orte: sucheResult.orte }))
        )
    );


    startEditSchule$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.startEditSchule),
            tap(() => this.#router.navigateByUrl('schulkatalog/schule-editor'))
        ), { dispatch: false });

    createSchule$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.createSchule),
            switchMap(action => this.#schulkatalogHttpService.createSchule(action.schulePayload)),
            map((schulePayload) => KatalogActions.createSchuleSuccess({ schulePayload }))
        )
    );

    updateSchule$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.updateSchule),
            switchMap(action => this.#schulkatalogHttpService.renameSchule(action.schulePayload)),
            map((schulePayload) => KatalogActions.updateSchuleSuccess({ schulePayload }))
        )
    );

    createSchuleSuccess$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.createSchuleSuccess),
            tap(() => this.#messageService.info('Schule erfolgreich angelegt'))
        ), { dispatch: false });

    updateSchuleSuccess$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.updateSchuleSuccess),
            tap(() => this.#messageService.info('Schule erfolgreich geändert'))
        ), { dispatch: false });


    startEditOrt$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.startEditOrt),
            tap(() => this.#router.navigateByUrl('schulkatalog/ort-editor'))
        ), { dispatch: false });



    updateOrt$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.updateOrt),
            switchMap(action => this.#schulkatalogHttpService.updateOrt(action.ortPayload)),
            map((ortPayload) => KatalogActions.updateOrtSuccess({ ortPayload }))
        )
    );

    updateOrtSuccess$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.updateOrtSuccess),
            tap(() => this.#messageService.info('Ort erfolgreich geändert'))
        ), { dispatch: false });

    startEditLand$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.startEditLand),
            tap(() => this.#router.navigateByUrl('schulkatalog/land-editor'))
        ), { dispatch: false });


    updateLand$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.updateLand),
            switchMap(action => this.#schulkatalogHttpService.updateLand(action.landPayload)),
            map((landPayload) => KatalogActions.updateLandSuccess({ landPayload }))
        )
    );

    updateLandSuccess$ = createEffect(
        () => this.#actions$.pipe(
            ofType(KatalogActions.updateLandSuccess),
            tap(() => this.#messageService.info('Land erfolgreich geändert'))
        ), { dispatch: false });
}