import { Injectable, inject } from "@angular/core";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";
import { Observable } from "rxjs";
import { AdminSchulkatalogFacade } from "../../admin-schulkatalog.facade";
import { Land } from "../../admin-katalog.model";

@Injectable({
    providedIn: 'root'
})
export class LaenderListResolver {

    #katalogFacade = inject(AdminSchulkatalogFacade);

    resolve(_route: ActivatedRouteSnapshot,
        _state: RouterStateSnapshot): Observable<Land[]> {

            this.#katalogFacade.loadLaender();
            return this.#katalogFacade.laender$;
    }

}