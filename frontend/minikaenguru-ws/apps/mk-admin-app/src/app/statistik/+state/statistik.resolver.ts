import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { StatistikFacade } from '../statistik.facade';

@Injectable()
export class StatistikResolver implements Resolve<any> {

    constructor(private statistikFacade: StatistikFacade) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {

       this.statistikFacade.expandStatistik('KINDER');
       this.statistikFacade.expandStatistik('LOESUNGSZETTEL');
        
    }

}
