import { AuthService } from '@minikaenguru-ws/common-auth';
import { LehrerFacade } from '../lehrer/lehrer.facade';
import { PrivatveranstalterFacade } from '../privatveranstalter/privatveranstalter.facade';
import { WettbewerbFacade } from '../wettbewerb/wettbewerb.facade';
import { TeilnahmenFacade } from '../teilnahmen/teilnahmen.facade';
import { Injectable } from '@angular/core';
import { VertragAdvFacade } from '../vertrag-adv/vertrag-adv.facade';


@Injectable({
	providedIn: 'root'
})
export class LogoutService {

	constructor(private authService: AuthService
		, private lehrerFacade: LehrerFacade
		, private privatveranstalterFacade: PrivatveranstalterFacade
		, private wettbewerbFacade: WettbewerbFacade
		, private teinahmenFacade: TeilnahmenFacade
		, private vertragAdvFacade: VertragAdvFacade) { }


	logout(): void {
		this.authService.logout();
		this.lehrerFacade.resetState();
		this.privatveranstalterFacade.resetState();
		this.wettbewerbFacade.resetState();
		this.teinahmenFacade.resetState();
		this.vertragAdvFacade.resetState();
	}
}
