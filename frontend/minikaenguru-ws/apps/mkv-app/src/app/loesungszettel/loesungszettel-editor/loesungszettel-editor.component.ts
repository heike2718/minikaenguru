import { Component, OnInit, Input, OnDestroy } from '@angular/core';
import { LoesungszettelFacade } from '../loesungszettel.facade';
import { KinderFacade } from '../../kinder/kinder.facade';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs';
import { Kind, kindToString } from '@minikaenguru-ws/common-components';

@Component({
	selector: 'mkv-loesungszettel-editor',
	templateUrl: './loesungszettel-editor.component.html',
	styleUrls: ['./loesungszettel-editor.component.css']
})
export class LoesungszettelEditorComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV'

	titel: string;

	private selectedKindSubscription: Subscription;

	private kind: Kind;

	constructor(public loesungszettelFacade: LoesungszettelFacade,
		private kinderFacade: KinderFacade,
		private router: Router) { }

	ngOnInit(): void {

		this.selectedKindSubscription = this.kinderFacade.selectedKind$.subscribe(
			kind => {
				this.kind = kind;

				if (kind) {
					this.titel = kindToString(this.kind) + ': Antworten erfassen oder ändern';
				} else {
					this.titel = 'kein Kind ausgewählt';
				}
			}
		)
	}

	ngOnDestroy(): void {

		if (this.selectedKindSubscription) {
			this.selectedKindSubscription.unsubscribe();
		}
	}

}
