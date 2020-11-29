import { Component, OnInit, Input, OnDestroy, ViewChild, TemplateRef } from '@angular/core';
import { Kind, Klasse, kindToString, Duplikatwarnung, KindEditorModel } from '@minikaenguru-ws/common-components';
import { KinderFacade } from '../kinder.facade';
import { Subscription, Observable, of } from 'rxjs';
import { FormBuilder, FormControl, Validators, FormGroup } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MessageService } from '@minikaenguru-ws/common-messages';
import { LogService } from '@minikaenguru-ws/common-logging';
import { KlassenwechselDaten } from '../kinder.model';
import { Router } from '@angular/router';
import { KlassenFacade } from '../../klassen/klassen.facade';
import { Schule } from '../../lehrer/schulen/schulen.model';
import { LehrerFacade } from '../../lehrer/lehrer.facade';

@Component({
	selector: 'mkv-klasse-wechseln',
	templateUrl: './klasse-wechseln.component.html',
	styleUrls: ['./klasse-wechseln.component.css']
})
export class KlasseWechselnComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	@ViewChild('dialogContent')
	dialogContent: TemplateRef<HTMLElement>;

	showGenericTitle = true;

	klassenwechselDaten$: Observable<KlassenwechselDaten>;

	kind: Kind;

	componentTitle = '';

	saveInProgress = false;

	selectDisabled = false;

	klassenNamen: string[];

	klassenForm: FormGroup;

	klassenFormControl: FormControl;

	duplikatwarnung: Duplikatwarnung;

	nameZielklasse: string;

	sourceKlasse: Klasse;

	private klassen: Klasse[];

	private selectedSchule: Schule;

	private showSaveMessage = false;

	private klassenwechselDatenSubscription: Subscription;

	private saveOutcomeSubscription: Subscription;

	private duplikatwarnungSubscription: Subscription;

	private selectedKlasseSubscription: Subscription;

	private schuleSubscription: Subscription;

	constructor(private fb: FormBuilder,
		private modalService: NgbModal,
		private kinderFacade: KinderFacade,
		private klassenFacade: KlassenFacade,
		private lehrerFacade: LehrerFacade,
		private router: Router,
		private messageService: MessageService,
		private logger: LogService) {


		this.initForm();

		if (this.kinderFacade.klassenwechselDaten$) {
			this.klassenwechselDaten$ = this.kinderFacade.klassenwechselDaten$;
		}
	}

	ngOnInit(): void {

		if (this.klassenwechselDaten$) {
			this.klassenwechselDatenSubscription = this.klassenwechselDaten$.subscribe(
				daten => {
					if (daten) {
						this.kind = daten.kind;
						this.klassen = daten.zielklassen;
						this.klassenNamen = this.klassen.map(k => k.name);
						this.componentTitle = kindToString(this.kind) + ' in andere Klasse verschieben';
						this.klassenForm.get('klasse').setValue(null, { onlySelf: true });
						this.showGenericTitle = false;
					} else {
						this.kind = undefined;
						this.klassen = [];
						this.componentTitle = 'keine Daten vorhanden';
						this.showGenericTitle = true;
					}
				}
			);
		}

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



		this.selectedKlasseSubscription = this.klassenFacade.selectedKlasse$.subscribe(
			kl => this.sourceKlasse = kl
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

		this.saveOutcomeSubscription = this.kinderFacade.saveOutcome$.subscribe(
			message => {
				if (this.showSaveMessage) {
					this.messageService.showMessage(message);
					this.showSaveMessage = false;
					this.saveInProgress = false;
					this.klassenForm.get('klasse').setValue(null, { onlySelf: true });
				}
			}
		);
	}

	ngOnDestroy(): void {

		if (this.klassenwechselDatenSubscription) {
			this.klassenwechselDatenSubscription.unsubscribe();
		}
		if (this.duplikatwarnungSubscription) {
			this.duplikatwarnungSubscription.unsubscribe();
		}
		if (this.selectedKlasseSubscription) {
			this.selectedKlasseSubscription.unsubscribe();
		}
		if (this.schuleSubscription) {
			this.schuleSubscription.unsubscribe();
		}
		if (this.saveOutcomeSubscription) {
			this.saveOutcomeSubscription.unsubscribe();
		}
	}

	editorInitialized(): boolean {

		if (!this.kind) {
			return false;
		}
		if (!this.klassenNamen || this.klassenNamen.length === 0) {
			return false;
		}
		return true;

	}

	onSubmit(): void {

		this.saveInProgress = true;
		const kindDaten = this.createRequestData();
		this.kinderFacade.pruefeDuplikat(this.kind.uuid, kindDaten);
	}

	onCancel(): void {

		this.messageService.clear();

		if (this.sourceKlasse) {
			this.router.navigateByUrl('/kinder/' + this.sourceKlasse.schulkuerzel);
		} else {
			this.router.navigateByUrl('/lehrer/dashboard');
		}
	}

	private initForm(): void {

		this.klassenFormControl = new FormControl({ value: '' }, Validators.required);

		this.klassenForm = this.fb.group({
			klasse: this.klassenFormControl
		});
	}

	private openWarndialog(content: TemplateRef<HTMLElement>) {

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
		this.saveInProgress = true;
		this.selectDisabled = true;
		const kindDaten = this.createRequestData();
		this.kinderFacade.updateKind(this.kind.uuid, kindDaten, this.selectedSchule);
	}

	private createRequestData(): KindEditorModel {

		const nameNeueKlasse = this.klassenForm.value.klasse;
		const neueKlassen = this.klassen.filter(kl => kl.name === nameNeueKlasse);
		const neueKlasse = neueKlassen[0];

		return {
			vorname: this.kind.vorname,
			nachname: this.kind.nachname,
			zusatz: this.kind.zusatz,
			klassenstufe: this.kind.klassenstufe,
			sprache: this.kind.sprache,
			klasseUuid: neueKlasse.uuid
		};
	}
}
