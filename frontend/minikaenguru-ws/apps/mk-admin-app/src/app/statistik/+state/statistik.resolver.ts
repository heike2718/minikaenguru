import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { StatistikFacade } from '../statistik.facade';

@Injectable()
export class StatistikResolver  {

    constructor(private statistikFacade: StatistikFacade) {}

    resolve(_route: ActivatedRouteSnapshot, _state: RouterStateSnapshot) {

       this.statistikFacade.expandStatistik('KINDER');
       this.statistikFacade.expandStatistik('DOWNLOADS');
       this.statistikFacade.expandStatistik('LOESUNGSZETTEL');
        
    }

}
