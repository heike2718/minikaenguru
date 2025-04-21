import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from "@angular/router";
import { DomainFacade } from "@mks/domain-api";


export const klassenstufeResolver: ResolveFn<any> = (
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot) => {
    return inject(DomainFacade).loadKlassenstufeDetails(route.params['id'], route.params['klassenstufe'])
};
