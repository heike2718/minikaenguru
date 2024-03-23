import { Component, OnInit, Input, OnDestroy, ViewChild, TemplateRef } from '@angular/core';
import { Kind, Klasse, kindToString, Duplikatwarnung, KindEditorModel, modalOptions } from '@minikaenguru-ws/common-components';
import { KinderFacade } from '../kinder.facade';
import { Subscription, Observable, of } from 'rxjs';
import { UntypedFormBuilder, UntypedFormControl, Validators, UntypedFormGroup } from '@angular/forms';
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

	devMode = environment.envName === 'DEV';

	@ViewChild('dialogContent')
	dialogContent!: TemplateRef<HTMLElement>;

	showGenericTitle = true;

	klassenwechselDaten$?: Observable<KlassenwechselDaten>;

	kind?: Kind;

	componentTitle = '';

	saveInProgress = false;

	selectDisabled = false;

	klassenNamen: string[] = [];

	klassenForm!: UntypedFormGroup;

	klassenFormControl!: UntypedFormControl;

	duplikatwarnung!: Duplikatwarnung;

	nameZielklasse: string = '';

	sourceKlasse?: Klasse;

	private klassen: Klasse[] = [];

	private selectedSchule?: Schule;

	private showSaveMessage = false;

	private klassenwechselDatenSubscription: Subscription = new Subscription();

	private saveOutcomeSubscription: Subscription = new Subscription();

	private duplikatwarnungSubscription: Subscription = new Subscription();

	private selectedKlasseSubscription: Subscription = new Subscription();

	private schuleSubscription: Subscription = new Subscription();

	constructor(private fb: UntypedFormBuilder,
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

						const control = this.klassenForm.get('klasse');
						if (control) {
							control.setValue(null, { onlySelf: true });
						}
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
				if (message && this.showSaveMessage) {
					this.messageService.showMessage(message);
					this.showSaveMessage = false;
					this.saveInProgress = false;

					const control = this.klassenForm.get('klasse');
					if (control) {
						control.setValue(null, { onlySelf: true });
					}
				}
			}
		);
	}

	ngOnDestroy(): void {

		this.klassenwechselDatenSubscription.unsubscribe();
		this.duplikatwarnungSubscription.unsubscribe();
		this.selectedKlasseSubscription.unsubscribe();
		this.schuleSubscription.unsubscribe();
		this.saveOutcomeSubscription.unsubscribe();
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

		if (!this.kind) {
			return;
		}
		const kindDaten = this.createRequestData();

		if (!kindDaten) {
			return;
		}

		this.saveInProgress = true;		
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

		this.klassenFormControl = new UntypedFormControl({ value: '' }, Validators.required);

		this.klassenForm = this.fb.group({
			klasse: this.klassenFormControl
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

		if (!this.kind) {
			return;
		}

		if (!this.selectedSchule) {
			return;
		}

		const kindDaten: KindEditorModel | undefined = this.createRequestData();

		if (!kindDaten) {
			return;
		}

		this.showSaveMessage = true;
		this.saveInProgress = true;
		this.selectDisabled = true;
		this.kinderFacade.moveKind(this.kind, kindDaten, this.selectedSchule);
	}

	private createRequestData(): KindEditorModel | undefined {

		if (!this.kind) {
			return undefined;
		}

		const nameNeueKlasse = this.klassenForm.value.klasse;
		const neueKlassen = this.klassen.filter(kl => kl.name === nameNeueKlasse);
		const neueKlasse = neueKlassen[0];

		return {
			vorname: this.kind.vorname,
			nachname: this.kind.nachname ? this.kind.nachname : '',
			zusatz: this.kind.zusatz ? this.kind.zusatz : '',
			klassenstufe: this.kind.klassenstufe,
			sprache: this.kind.sprache,
			klasseUuid: neueKlasse.uuid
		};
	}
}
