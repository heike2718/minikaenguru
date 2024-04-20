import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from "@angular/router";
import { DomainFacade } from "@mkbiza-app/domain-api";


export const klassenstufeResolver: ResolveFn<any> = (
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot) => {
    return inject(DomainFacade).getKlassenstufeDetails(route.params['id'], route.params['klassenstufe'])
};
