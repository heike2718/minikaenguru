import { inject } from "@angular/core";
import { ActivatedRouteSnapshot, ResolveFn, RouterStateSnapshot } from "@angular/router";
import { DomainFacade } from "@mks/domain-api";

export const wettbewerbDetailsResolver: ResolveFn<any> = (
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot) => {
    return inject(DomainFacade).loadWettbewerbsdetails(route.params['id'])
};
