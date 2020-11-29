import { Component, OnInit, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { KinderFacade } from '../kinder.facade';
import { FormBuilder, FormGroup, FormControl, Validators } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { LogService } from '@minikaenguru-ws/common-logging';
import { MessageService } from '@minikaenguru-ws/common-messages';
import {
	KindEditorModel,
	Klassenstufe,
	Sprache,
	ALL_KLASSENSTUFEN,
	ALL_SPRACHEN,
	getKlassenstufeByLabel,
	Duplikatwarnung,
	getSpracheByLabel,
	TeilnahmeIdentifier,
	TeilnahmeIdentifierAktuellerWettbewerb
} from '@minikaenguru-ws/common-components';
import { Subscription } from 'rxjs';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { LehrerFacade } from '../../lehrer/lehrer.facade';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { User, Rolle } from '@minikaenguru-ws/common-auth';

@Component({
	selector: 'mkv-kind-editor',
	templateUrl: './kind-editor.component.html',
	styleUrls: ['./kind-editor.component.css']
})
export class KindEditorComponent implements OnInit, OnDestroy {

	@ViewChild('dialogContent')
	dialogContent: TemplateRef<HTMLElement>;

	devMode = !environment.production;

	kindForm: FormGroup;

	vornameFormControl: FormControl;

	nachnameFormControl: FormControl;

	zusatzFormControl: FormControl;

	klassenstufeFormControl: FormControl;

	spracheFormControl: FormControl;

	klassenstufen: string[];

	sprachen: string[];

	saveInProgress = false;

	duplikatwarnung: Duplikatwarnung;

	editorInitialized = false;

	showWarndialog = false;

	private selectedSchule: Schule;

	private showSaveMessage = false;

	private uuid: string;

	private teilnahmeIdentifier: TeilnahmeIdentifierAktuellerWettbewerb;

	private initialGuiModel: KindEditorModel;

	private editorModelSubscription: Subscription;

	private duplikatwarnungSubscription: Subscription;

	private kindDaten: KindEditorModel;

	private routeParamsSubcription: Subscription;

	private queryParamsSubscription: Subscription;

	private saveOutcomeSubscription: Subscription;

	private teilnahmeIdentifierSubscription: Subscription;

	private klasseUuid: string;

	private schuleSubscription: Subscription;

	constructor(private fb: FormBuilder,
		private modalService: NgbModal,
		private kinderFacade: KinderFacade,
		private lehrerFacade: LehrerFacade,
		private router: Router,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private logger: LogService) {

		this.klassenstufen = ALL_KLASSENSTUFEN.map(kl => kl.label);
		this.sprachen = ALL_SPRACHEN.map(sp => sp.label);

		this.initForm();
	}

	ngOnInit(): void {

		this.routeParamsSubcription = this.route.params.subscribe(
			p => this.uuid = p['id']
		);

		this.queryParamsSubscription = this.route.queryParams.subscribe(
			p => this.klasseUuid = p['klasseUuid']
		);

		this.editorModelSubscription = this.kinderFacade.kindEditorModel$.subscribe(

			ke => {

				if (ke) {

					this.initialGuiModel = { ...ke };
					this.kindForm.get('vorname').setValue(this.initialGuiModel.vorname, { onlySelf: true });
					this.kindForm.get('nachname').setValue(this.initialGuiModel.nachname, { onlySelf: true });
					this.kindForm.get('zusatz').setValue(this.initialGuiModel.zusatz, { onlySelf: true });
					this.kindForm.get('klassenstufe').setValue(this.initialGuiModel.klassenstufe ? this.initialGuiModel.klassenstufe.label : null, { onlySelf: true });
					this.kindForm.get('sprache').setValue(this.initialGuiModel.sprache ? this.initialGuiModel.sprache.label : null, { onlySelf: true });


					this.editorInitialized = true;
					if (!this.klasseUuid) {
						this.klasseUuid = ke.klasseUuid;
					}
				}
			}
		);

		this.duplikatwarnungSubscription = this.kinderFacade.duplikatwarnung$.subscribe(
			warnung => {

				if (warnung) {
					if (warnung.kontext === 'KIND') {

						const text = warnung.warnungstext;

						if (text.length === 0) {
							this.saveKind();
						} else {
							this.showWarndialog = true;
							this.duplikatwarnung = warnung;

							this.open(this.dialogContent);
						}
					}
				}
			}
		);

		this.saveOutcomeSubscription = this.kinderFacade.saveOutcome$.subscribe(
			message => {
				if (this.showSaveMessage) {
					this.messageService.showMessage(message);
					this.showSaveMessage = false;
					this.saveInProgress = false;
				}
			}
		);

		this.teilnahmeIdentifierSubscription = this.kinderFacade.teilnahmeIdentifier$.subscribe(
			ti => this.teilnahmeIdentifier = ti
		);

		if (this.lehrerFacade) {

			this.schuleSubscription = this.lehrerFacade.selectedSchule$.subscribe(

				sch => {
					if (sch) {
						this.selectedSchule = sch;
					}
				}

			);
		}
	}

	ngOnDestroy(): void {
		if (this.editorModelSubscription) {
			this.editorModelSubscription.unsubscribe();
		}

		if (this.duplikatwarnungSubscription) {
			this.duplikatwarnungSubscription.unsubscribe();
		}

		if (this.routeParamsSubcription) {
			this.routeParamsSubcription.unsubscribe();
		}

		if (this.queryParamsSubscription) {
			this.queryParamsSubscription.unsubscribe();
		}

		if (this.saveOutcomeSubscription) {
			this.saveOutcomeSubscription.unsubscribe();
		}

		if (this.teilnahmeIdentifierSubscription) {
			this.teilnahmeIdentifierSubscription.unsubscribe();
		}

		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}
	}

	onCancel(): void {
		this.messageService.clear();
		this.kinderFacade.cancelEditKind();

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

	onSubmit(): void {

		this.saveInProgress = true;
		const formValue = this.kindForm.value;

		this.kindDaten = {
			vorname: formValue.vorname.trim(),
			nachname: formValue.nachname === '' ? null : formValue.nachname.trim(),
			zusatz: formValue.zusatz === '' ? null : formValue.zusatz.trim(),
			sprache: getSpracheByLabel(formValue.sprache),
			klassenstufe: getKlassenstufeByLabel(formValue.klassenstufe)
		};

		if (this.klasseUuid) {
			this.kindDaten = { ...this.kindDaten, klasseUuid: this.klasseUuid };
		}

		this.kinderFacade.pruefeDuplikat(this.uuid, this.kindDaten);
	}


	private initForm(): void {

		this.vornameFormControl = new FormControl({ value: '' }, { validators: [Validators.required, Validators.maxLength(55)] });
		this.nachnameFormControl = new FormControl({ value: '' }, { validators: [Validators.maxLength(55)] });
		this.zusatzFormControl = new FormControl({ value: '' }, { validators: [Validators.maxLength(55)] });
		this.klassenstufeFormControl = new FormControl({ value: '' }, { validators: [Validators.required] });
		this.spracheFormControl = new FormControl({ value: '' }, Validators.required);

		this.kindForm = this.fb.group({
			vorname: this.vornameFormControl,
			nachname: this.nachnameFormControl,
			zusatz: this.zusatzFormControl,
			klassenstufe: this.klassenstufeFormControl,
			sprache: this.spracheFormControl
		});
	}


	private open(content: TemplateRef<HTMLElement>) {

		this.saveInProgress = false;
		this.modalService.open(content, { ariaLabelledBy: 'modal-basic-title' }).result.then((result) => {

			if (result === 'ja') {
				this.saveKind();
			}

		}, (reason) => {
			this.logger.debug('closed with reason=' + reason);
		});
	}

	private saveKind(): void {
		this.showSaveMessage = true;
		if (this.uuid === 'neu') {
			this.kinderFacade.insertKind(this.uuid, this.kindDaten, this.selectedSchule);
		} else {
			this.kinderFacade.updateKind(this.uuid, this.kindDaten, this.selectedSchule);
		}
	}


	addKind(): void {

		this.messageService.clear();

		this.saveInProgress = false;
		this.showWarndialog = false;
		this.editorInitialized = false;

		this.kinderFacade.createNewKind(this.klasseUuid);

		const url = '/kinder/kind-editor/neu';

		if (this.klasseUuid) {
			this.router.navigate([url], { queryParams: { klasseUuid: this.klasseUuid } });
		} else {
			this.router.navigateByUrl(url);
		}
	}


}
