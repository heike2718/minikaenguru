import { Component, OnInit, OnDestroy } from '@angular/core';
import { Wettbewerb } from '../../wettbewerb/wettbewerb.model';
import { WettbewerbFacade } from '../../wettbewerb/wettbewerb.facade';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs';
import { LehrerFacade } from '../lehrer.facade';
import { User, STORAGE_KEY_USER } from '@minikaenguru-ws/common-auth';

@Component({
	selector: 'mkv-unterlagen-card',
	templateUrl: './unterlagen-card.component.html',
	styleUrls: ['./unterlagen-card.component.css']
})
export class UnterlagenCardComponent implements OnInit, OnDestroy {

	unterlagenDeutschUrl = environment.apiUrl + '/unterlagen/schulen/de';
	unterlagenEnglischUrl = environment.apiUrl + '/unterlagen/schulen/en';

	userIdRef: string;

	hatZugangZuUnterlagen = false;

	private zugangUnterlagenSubscription: Subscription;


	constructor(public wettbewerbFacade: WettbewerbFacade, private lehrerFacade: LehrerFacade) { }

	ngOnInit(): void {
		const user: User = JSON.parse(localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER));
		this.userIdRef = user.idReference;

		this.zugangUnterlagenSubscription = this.lehrerFacade.hatZugangZuUnterlagen$.subscribe(hat => {
			this.hatZugangZuUnterlagen = hat;
		});
	}

	ngOnDestroy(): void {

		if (this.zugangUnterlagenSubscription) {
			this.zugangUnterlagenSubscription.unsubscribe();
		}
	}

}
