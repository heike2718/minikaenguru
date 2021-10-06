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
import { LogService } from '@minikaenguru-ws/common-logging';
import { TeilnahmenListResolver } from '../../teilnahmen/teilnahmen-list/teilnahmen-list.resolver';

@Component({
	selector: 'mkv-loesungszettel-editor',
	templateUrl: './loesungszettel-editor.component.html',
	styleUrls: ['./loesungszettel-editor.component.css']
})
export class LoesungszettelEditorComponent implements OnInit, OnDestroy {

	@ViewChild('dialogContentLeererLoesungszettel')
	dialogContentLeererLoesungszettel!: TemplateRef<HTMLElement>;

	@ViewChild('dialogContentLoeschen')
	dialogContentLoeschen!: TemplateRef<HTMLElement>;

	devMode = environment.envName === 'DEV'

	titel: string = '';

	nameKind: string = '';

	loesungszetteleingaben: string = '';

	saveInProgress = false;

	showSaveMessage = false;

	uuid?: string;

	loesungszettel?: Loesungszettel;

	kind?: Kind;

	private selectedKindSubscription: Subscription = new Subscription();

	private loesungszettelSubscription: Subscription = new Subscription();

	private teilnahmeIdentifierSubscription: Subscription = new Subscription();		

	private teilnahmeIdentifier?: TeilnahmeIdentifierAktuellerWettbewerb;


	constructor(private loesungszettelFacade: LoesungszettelFacade,
		private kinderFacade: KinderFacade,
		private modalService: NgbModal,
		private messageService: MessageService,
		private logger: LogService,
		private router: Router) { }

	ngOnInit(): void {

		this.selectedKindSubscription = this.kinderFacade.selectedKind$.subscribe(
			kind => {

				if (kind) {
					this.nameKind = kindToString(kind);
					this.titel = this.nameKind + ': Antworten erfassen oder ändern';
				} else {
					this.titel = 'kein Kind ausgewählt';
				}

				this.kind = kind;				
			}
		);

		this.loesungszettelSubscription = this.loesungszettelFacade.selectedLoesungszettel$.subscribe(

			zettel => {

				if (zettel) {
					if (!zettel.zeilen) {
						// Lösungszettel wurde vermutlich konkurrierend geöscht!
						this.messageService.warn('Der Lösungszettel wurde möglicherweise inzwischen gelöscht. Bitte klären Sie das im Kollegium.');
						this.onCancel();
					}

					this.loesungszetteleingaben = this.initEingaben(zettel);
					this.uuid = zettel.uuid;
				}

				this.loesungszettel = zettel;
			}

		);
		this.teilnahmeIdentifierSubscription = this.kinderFacade.teilnahmeIdentifier$.subscribe(
			ti => this.teilnahmeIdentifier = ti
		);

	}

	ngOnDestroy(): void {
		this.selectedKindSubscription.unsubscribe();
		this.loesungszettelSubscription.unsubscribe();
		this.teilnahmeIdentifierSubscription.unsubscribe();
	}

	onSubmit(): void {

		if (!this.loesungszettel) {
			this.logger.debug('selectedLoesungszettel was undefined');
			return;
		}
		if (!this.kind) {
			this.logger.debug('kind was undefined');
			return;
		}

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

		if (!this.loesungszettel) {
			this.logger.debug('selectedLoesungszette was undefined');
			return;
		}
		if (!this.kind) {
			this.logger.debug('kind was undefined');
			return;
		}
		this.openWarndialogLoesungszettelLoeschen();
	}

	private forceDelete(): void {
		if (!this.kind || !this.loesungszettel) {
			return;
		}
		this.loesungszettelFacade.deleteLoesungszettel(this.kind, this.loesungszettel);
		this.navigateBack();
	}

	private forceSave(): void {

		if (!this.kind || !this.loesungszettel) {
			return;
		}

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
