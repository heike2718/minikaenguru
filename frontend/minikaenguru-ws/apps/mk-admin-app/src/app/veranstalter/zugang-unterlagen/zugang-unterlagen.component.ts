import { Component, OnInit, OnDestroy } from '@angular/core';
import { VeranstalterFacade } from '../veranstalter.facade';
import { Veranstalter } from '../veranstalter.model';
import { Observable, Subscription } from 'rxjs';
import { ZugangsstatusButtons, ZugangsstatusButtonModel, createZugangUnterlagenButtons } from './zugang-unterlagen.model';

@Component({
	selector: 'mka-zugang-unterlagen',
	templateUrl: './zugang-unterlagen.component.html',
	styleUrls: ['./zugang-unterlagen.component.css']
})
export class ZugangUnterlagenComponent implements OnInit, OnDestroy {

	selectedVeranstalter$: Observable<Veranstalter> = this.veranstalterFacade.selectedVeranstalter$;

	buttons: ZugangsstatusButtons;

	private veranstalter: Veranstalter;
	private selectedVeranstalterSubscription: Subscription;



	constructor(private veranstalterFacade: VeranstalterFacade) { }

	ngOnInit(): void {


		this.selectedVeranstalterSubscription = this.selectedVeranstalter$.subscribe(veranstalter => {
			this.veranstalter = veranstalter;

			if (veranstalter) {
				this.buttons = createZugangUnterlagenButtons(veranstalter.zugangsstatusUnterlagen);
			} else {
				this.buttons = {
					buttons: []
				};
			}
		});

	}

	ngOnDestroy(): void {

		if (this.selectedVeranstalterSubscription) {
			this.selectedVeranstalterSubscription.unsubscribe();
		}
	}

	onButtonClicked(btn: ZugangsstatusButtonModel): void {

		this.veranstalterFacade.zugangsstatusUnterlagenAendern(this.veranstalter, btn.neuerStatus);
	}

}
