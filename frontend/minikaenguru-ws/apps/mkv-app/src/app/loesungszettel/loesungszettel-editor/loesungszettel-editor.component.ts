import { Component, OnInit, Input, OnDestroy, ViewChild, TemplateRef } from '@angular/core';
import { LoesungszettelFacade } from '../loesungszettel.facade';
import { KinderFacade } from '../../kinder/kinder.facade';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs';
import { Kind, kindToString, TeilnahmeIdentifierAktuellerWettbewerb } from '@minikaenguru-ws/common-components';
import { Loesungszettel, loesungszettelIsLeer } from '../loesungszettel.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { User } from '@minikaenguru-ws/common-auth';
import { modalOptions } from '../../shared/utils';

@Component({
	selector: 'mkv-loesungszettel-editor',
	templateUrl: './loesungszettel-editor.component.html',
	styleUrls: ['./loesungszettel-editor.component.css']
})
export class LoesungszettelEditorComponent implements OnInit, OnDestroy {

	@ViewChild('dialogContentLeererLoesungszettel')
	dialogContentLeererLoesungszettel: TemplateRef<HTMLElement>;

	@ViewChild('dialogContentLoeschen')
	dialogContentLoeschen: TemplateRef<HTMLElement>;

	devMode = environment.envName === 'DEV'

	titel: string;

	nameKind: string;

	loesungszetteleingaben: string;

	saveInProgress = false;

	showSaveMessage = false;

	uuid: string;

	private selectedKindSubscription: Subscription;

	private loesungszettelSubscription: Subscription;

	private teilnahmeIdentifierSubscription: Subscription;

	private loesungszettel: Loesungszettel;

	private kind: Kind;

	private teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb;


	constructor(public loesungszettelFacade: LoesungszettelFacade,
		private kinderFacade: KinderFacade,
		private modalService: NgbModal,
		private messageService: MessageService,
		private router: Router) { }

	ngOnInit(): void {

		this.selectedKindSubscription = this.kinderFacade.selectedKind$.subscribe(
			kind => {
				this.kind = kind;

				if (kind) {
					this.nameKind = kindToString(this.kind);
					this.titel = this.nameKind + ': Antworten erfassen oder ändern';
				} else {
					this.titel = 'kein Kind ausgewählt';
				}
			}
		);

		this.loesungszettelSubscription = this.loesungszettelFacade.selectedLoesungszettel$.subscribe(

			zettel => {

				if (zettel && !zettel.zeilen) {
					// Lösungszettel wurde vermutlich konkurrierend geöscht!
					this.messageService.warn('Der Lösungszettel wurde möglicherweise inzwischen gelöscht. Bitte klären Sie das im Kollegium.');
					this.onCancel();
				}

				this.loesungszetteleingaben = this.initEingaben(zettel);
				this.loesungszettel = zettel;

				if (zettel) {
					this.uuid = zettel.uuid;
				} else {
					this.uuid = undefined;
				}
			}

		);
		this.teilnahmeIdentifierSubscription = this.kinderFacade.teilnahmeIdentifier$.subscribe(
			ti => this.teilnahmeIdentifier = ti
		);

	}

	ngOnDestroy(): void {

		if (this.selectedKindSubscription) {
			this.selectedKindSubscription.unsubscribe();
		}

		if (this.loesungszettelSubscription) {
			this.loesungszettelSubscription.unsubscribe();
		}

		if (this.teilnahmeIdentifierSubscription) {
			this.teilnahmeIdentifierSubscription.unsubscribe();
		}
	}

	onSubmit(): void {

		this.saveInProgress = true;

		if (loesungszettelIsLeer(this.loesungszettel)) {
			this.openWarndialogLeererLoesungszettel();
		} else {
			this.forceSave();
		}
	}

	onCancel(): void {

		this.messageService.clear();
		this.loesungszettelFacade.cancelEditLoesungszettel();
		this.navigateBack();
	}

	onDelete(): void {
		this.openWarndialogLoesungszettelLoeschen();
	}

	private forceDelete(): void {
		this.loesungszettelFacade.deleteLoesungszettel(this.kind, this.loesungszettel);
		this.navigateBack();
	}

	private forceSave(): void {

		this.saveInProgress = false;
		this.loesungszettelFacade.saveLoesungszettel(this.kind, this.loesungszettel);
		this.showSaveMessage = true;

	}

	private navigateBack(): void {

		if (this.teilnahmeIdentifier && this.teilnahmeIdentifier.teilnahmenummer) {
			this.router.navigateByUrl('/kinder/' + this.teilnahmeIdentifier.teilnahmenummer);
		} else {

			const item = localStorage.getItem(environment.storageKeyPrefix + 'user');
			if (item) {
				const user: User = JSON.parse(item);

				switch (user.rolle) {
					case 'LEHRER':
						this.router.navigateByUrl('/lehrer/dashboard');
						break;
					case 'PRIVAT':
						this.router.navigateByUrl('/privat/dashboard');
						break;
					default: this.router.navigateByUrl('/landing');
				}
			}
		}
	}

	private openWarndialogLeererLoesungszettel() {

		this.saveInProgress = false;
		this.modalService.open(this.dialogContentLeererLoesungszettel, modalOptions).result.then((result) => {

			if (result === 'ja') {
				this.forceSave();
			}

		});
	}

	private openWarndialogLoesungszettelLoeschen() {

		this.modalService.open(this.dialogContentLoeschen, modalOptions).result.then((result) => {

			if (result === 'ja') {
				this.forceDelete();
			}

		});
	}

	private initEingaben(loesungszettel: Loesungszettel): string {

		let eingaben = '';

		if (!loesungszettel || !loesungszettel.zeilen) {
			return '';
		}

		if (loesungszettel) {


			for (let i = 0; i < loesungszettel.zeilen.length; i++) {
				eingaben += loesungszettel.zeilen[i].eingabe;
			}


		}

		return eingaben;
	}
}
