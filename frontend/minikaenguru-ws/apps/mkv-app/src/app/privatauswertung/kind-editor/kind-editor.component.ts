import { Component, OnInit, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { PrivatauswertungFacade } from '../privatauswertung.facade';
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
	getSpracheByLabel
} from '@minikaenguru-ws/common-components';
import { Subscription } from 'rxjs';
import { NgbModal, ModalDismissReasons } from '@ng-bootstrap/ng-bootstrap';

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

	showWarndialog = true;

	private showSaveMessage = false;

	private uuid: string;

	private initialGuiModel: KindEditorModel;

	private editorModelSubscription: Subscription;

	private duplikatwarnungSubscription: Subscription;

	private kindDaten: KindEditorModel;

	private routeParamsSubcription: Subscription;

	private saveOutcomeSubscription: Subscription;

	constructor(private fb: FormBuilder,
		private modalService: NgbModal,
		private privatauswertungFacade: PrivatauswertungFacade,
		private router: Router,
		private route: ActivatedRoute,
		private messageService: MessageService,
		private logger: LogService) {

		this.klassenstufen = ALL_KLASSENSTUFEN.map(kl => kl.label);
		this.sprachen = ALL_SPRACHEN.map(sp => sp.label);

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

	ngOnInit(): void {

		this.routeParamsSubcription = this.route.params.subscribe(
			p => {
				this.uuid = p['id'];
			}
		);

		this.editorModelSubscription = this.privatauswertungFacade.kindEditorModel$.subscribe(

			ke => {

				if (ke) {

					this.initialGuiModel = { ...ke };
					this.kindForm.get('vorname').setValue(this.initialGuiModel.vorname, { onlySelf: true });
					this.kindForm.get('nachname').setValue(this.initialGuiModel.nachname, { onlySelf: true });
					this.kindForm.get('zusatz').setValue(this.initialGuiModel.zusatz, { onlySelf: true });
					this.kindForm.get('klassenstufe').setValue(this.initialGuiModel.klassenstufe ? this.initialGuiModel.klassenstufe.label : null, { onlySelf: true });
					this.kindForm.get('sprache').setValue(this.initialGuiModel.sprache.label, { onlySelf: true });


					this.editorInitialized = true;
				}
			}
		);

		this.duplikatwarnungSubscription = this.privatauswertungFacade.duplikatwarnung$.subscribe(
			warnung => {

				if (warnung && warnung.kontext === 'KIND') {
					if (warnung.warnungstext === '') {

						this.saveKind();
					} else {
						this.showWarndialog = true;
						this.duplikatwarnung = warnung;

						this.open(this.dialogContent);
					}
				}
			}
		);

		this.saveOutcomeSubscription = this.privatauswertungFacade.saveOutcome$.subscribe(
			message => {
				if (this.showSaveMessage) {
					this.messageService.showMessage(message);
					this.showSaveMessage = false;
				}
			}
		);
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
		if (this.saveOutcomeSubscription) {
			this.saveOutcomeSubscription.unsubscribe();
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

		this.privatauswertungFacade.pruefeDuplikat(this.uuid, this.kindDaten);
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

	onCancel(): void {
		this.messageService.clear();
		this.privatauswertungFacade.cancelEditKind();
		this.router.navigateByUrl('/privatauswertung');
	}

	saveKind(): void {
		this.showSaveMessage = true;
		if (this.uuid === 'neu') {
			this.privatauswertungFacade.insertKind(this.uuid, this.kindDaten);
		} else {
			this.privatauswertungFacade.updateKind(this.uuid, this.kindDaten);
		}
	}
}
