import { Component, OnInit, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { KinderFacade } from '../kinder.facade';
import { UntypedFormBuilder, UntypedFormGroup, UntypedFormControl, Validators, AbstractControl } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { LogService } from '@minikaenguru-ws/common-logging';
import { MessageService } from '@minikaenguru-ws/common-messages';
import {
	KindEditorModel,
	ALL_KLASSENSTUFEN,
	ALL_SPRACHEN,
	getKlassenstufeByLabel,
	Duplikatwarnung,
	getSpracheByLabel,
	TeilnahmeIdentifierAktuellerWettbewerb,
	initialKindEditorModel,
	modalOptions
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
	dialogContent!: TemplateRef<HTMLElement>;

	devMode = environment.envName === 'DEV';

	kindForm!: UntypedFormGroup;

	vornameFormControl!: UntypedFormControl;

	nachnameFormControl!: UntypedFormControl;

	zusatzFormControl!: UntypedFormControl;

	klassenstufeFormControl!: UntypedFormControl;

	spracheFormControl!: UntypedFormControl;

	klassenstufen: string[];

	sprachen: string[];

	saveInProgress = false;

	duplikatwarnung?: Duplikatwarnung;

	editorInitialized = false;

	private selectedSchule?: Schule;

	private showSaveMessage = false;

	private teilnahmeIdentifier?: TeilnahmeIdentifierAktuellerWettbewerb;

	private initialGuiModel: KindEditorModel = initialKindEditorModel;

	private editorModelSubscription: Subscription = new Subscription();

	private duplikatwarnungSubscription: Subscription = new Subscription();

	private kindDaten!: KindEditorModel;

	private queryParamsSubscription: Subscription = new Subscription();

	private saveOutcomeSubscription: Subscription = new Subscription();

	private teilnahmeIdentifierSubscription: Subscription = new Subscription();

	private klasseUuid?: string;

	private selectedKindUUID?: string;

	private schuleSubscription: Subscription = new Subscription();

	private selectedKindSubscription: Subscription = new Subscription();

	constructor(private fb: UntypedFormBuilder,
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

		this.queryParamsSubscription = this.route.queryParams.subscribe(
			p => this.klasseUuid = p['klasseUuid']
		);

		this.selectedKindSubscription = this.kinderFacade.selectedKind$.subscribe(
			kind => {
				if (kind) {
					this.selectedKindUUID = kind.uuid;
				} else {
					this.selectedKindUUID = undefined;
				}
			}
		);

		this.editorModelSubscription = this.kinderFacade.kindEditorModel$.subscribe(

			ke => {

				if (ke) {

					this.initialGuiModel = { ...ke };

					{
						const control: AbstractControl | null = this.kindForm.get('vorname');
						if (control) {
							control.setValue(this.initialGuiModel.vorname, { onlySelf: true });
						}
					}

					{
						const control: AbstractControl | null = this.kindForm.get('nachname');
						if (control) {
							control.setValue(this.initialGuiModel.nachname, { onlySelf: true });
						}
					}

					{
						const control: AbstractControl | null = this.kindForm.get('zusatz');
						if (control) {
							control.setValue(this.initialGuiModel.zusatz, { onlySelf: true });
						}
					}

					{
						const control: AbstractControl | null = this.kindForm.get('klassenstufe');
						if (control) {
							control.setValue(this.initialGuiModel.klassenstufe.label, { onlySelf: true });
						}
					}

					{
						const control: AbstractControl | null = this.kindForm.get('sprache');
						if (control) {
							control.setValue(this.initialGuiModel.sprache ? this.initialGuiModel.sprache.label : null, { onlySelf: true });
						}
					}

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
							this.duplikatwarnung = warnung;
							this.openWarndialog(this.dialogContent);
						}
					}
				}
			}
		);

		this.saveOutcomeSubscription = this.kinderFacade.saveOutcome$.subscribe(
			message => {
				if (message && this.showSaveMessage) {
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

		this.selectedKindSubscription.unsubscribe();
		this.editorModelSubscription.unsubscribe();
		this.duplikatwarnungSubscription.unsubscribe();
		this.queryParamsSubscription.unsubscribe();
		this.saveOutcomeSubscription.unsubscribe();
		this.teilnahmeIdentifierSubscription.unsubscribe();
		this.schuleSubscription.unsubscribe();
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

		if (!this.selectedKindUUID) {
			this.logger.debug('selectedKindUUID was undefined');
			return;
		}

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

		this.kinderFacade.pruefeDuplikat(this.selectedKindUUID, this.kindDaten);
	}


	private initForm(): void {

		this.vornameFormControl = new UntypedFormControl({ value: '' }, { validators: [Validators.required, Validators.maxLength(55)] });
		this.nachnameFormControl = new UntypedFormControl({ value: '' }, { validators: [Validators.maxLength(55)] });
		this.zusatzFormControl = new UntypedFormControl({ value: '' }, { validators: [Validators.maxLength(55)] });
		this.klassenstufeFormControl = new UntypedFormControl({ value: '' }, { validators: [Validators.required] });
		this.spracheFormControl = new UntypedFormControl({ value: '' }, Validators.required);

		this.kindForm = this.fb.group({
			vorname: this.vornameFormControl,
			nachname: this.nachnameFormControl,
			zusatz: this.zusatzFormControl,
			klassenstufe: this.klassenstufeFormControl,
			sprache: this.spracheFormControl
		});
	}


	private openWarndialog(content: TemplateRef<HTMLElement>) {

		this.saveInProgress = false;
		this.modalService.open(content, modalOptions).result.then((result) => {

			if (result === 'ja') {
				this.saveKind();
			}

		}, (reason) => {
			this.logger.debug('closed with reason=' + reason);
		});
	}

	private saveKind(): void {

		if (!this.selectedKindUUID) {
			this.logger.debug('selectedKindUUID wa undefined');
			return;
		}

		this.showSaveMessage = true;
		if (this.selectedKindUUID === 'neu') {
			this.kinderFacade.insertKind(this.selectedKindUUID, this.kindDaten, this.selectedSchule);
		} else {
			this.kinderFacade.updateKind(this.selectedKindUUID, this.kindDaten, this.selectedSchule);
		}
	}


	addKind(): void {

		if (!this.klasseUuid) {
			this.logger.debug('klasseUUId was undefined');
			return;
		}

		this.messageService.clear();

		this.saveInProgress = false;
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
