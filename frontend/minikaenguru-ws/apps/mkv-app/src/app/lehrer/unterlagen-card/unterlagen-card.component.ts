import { Component, OnInit, OnDestroy } from '@angular/core';
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

	userIdRef!: string;

	hatZugangZuUnterlagen = false;

	private zugangUnterlagenSubscription: Subscription = new Subscription();


	constructor(public wettbewerbFacade: WettbewerbFacade, private lehrerFacade: LehrerFacade) { }

	ngOnInit(): void {
		const obj: string | null = localStorage.getItem(environment.storageKeyPrefix + STORAGE_KEY_USER);
		if (obj) {
			const user: User = JSON.parse(obj);
			this.userIdRef = user.idReference;
		} else {
			this.userIdRef = '';
		}

		this.zugangUnterlagenSubscription = this.lehrerFacade.hatZugangZuUnterlagen$.subscribe(hat => {
				if (hat !== undefined) {
					this.hatZugangZuUnterlagen = hat;
				}
		});
	}

	ngOnDestroy(): void {
		this.zugangUnterlagenSubscription.unsubscribe();
	}

}
