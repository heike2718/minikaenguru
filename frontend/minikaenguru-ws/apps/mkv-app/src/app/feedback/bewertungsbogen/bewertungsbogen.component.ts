import { Component, OnDestroy, OnInit, inject } from "@angular/core";
import { FeedbackFacade } from "../feedback.facade";
import { environment } from "apps/mkv-app/src/environments/environment";
import { FormArray, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { Subscription, combineLatest } from "rxjs";
import {
	BewertungAufgabeGUIModel,
	BewertungsbogenGUIModel,
	schwierigkeitsgradOptions,
	kategorieOptions,
	jaNeinOptions,
	spassOptions,
	zufriedenheitOptions,
	BewertungsbogenKlassenstufe,
	mapFormValueToBewertungsbogen,
	isBewertungsbogenLeer
} from "../feedback.model";
import { Router } from "@angular/router";
import { Klassenstufenart, getKlassenstufeByLabel } from "@minikaenguru-ws/common-components";

@Component({
	selector: 'mkv-bewertungsbogen',
	templateUrl: './bewertungsbogen.component.html',
	styleUrls: ['./bewertungsbogen.component.css']
})
export class BewertungsbogenComponent implements OnInit, OnDestroy {

	devMode = environment.envName === 'DEV';

	form!: FormGroup;
	items!: FormArray;

	feedbackFacade = inject(FeedbackFacade);

	schwierigkeitsgradOptions: string[] = schwierigkeitsgradOptions;
	kategorieOptions: string[] = kategorieOptions;
	jaNeinOptions: string[] = jaNeinOptions;
	spassOptions: string[] = spassOptions;
	zufriedenheitOptions: string[] = zufriedenheitOptions;

	#bewertungsbogenGUIModel!: BewertungsbogenGUIModel;
	#fb = inject(FormBuilder);
	#router = inject(Router);

	#bewertungSubmitted = false;
	#bewertungsbogenEINSSubmitted = false;
	#bwertungsbogenZWEISubmitted = false;

	showFormular = true;


	#bewertungCombinedSubscription = new Subscription();

	constructor() {

		this.items = this.#fb.array([]);

		this.form = this.#fb.group({
			scoreSpass: ['0'],
			scoreZufriedenheit: ['0'],
			freitextWettbewerb: [''], // TODO: pattern
			items: this.items
		});
	}

	ngOnInit(): void {

		this.#bewertungCombinedSubscription = combineLatest([
			this.feedbackFacade.bewertungsformularModel$,
			this.feedbackFacade.bewertungsboegenSubmitted$,
			this.feedbackFacade.bewertungsbogenEINSSubmitted$,
			this.feedbackFacade.bewertungsbogenZWEISubmitted$
		]).subscribe(([model, submitted, eINSSubmitted, zWEISubmitted]) => {
			this.#bewertungsbogenGUIModel = model;
			this.#initForm();
			this.#bewertungSubmitted = submitted;
			this.#bewertungsbogenEINSSubmitted = eINSSubmitted;
			this.#bwertungsbogenZWEISubmitted = zWEISubmitted;

			if (this.#bewertungsbogenEINSSubmitted && this.#getTheKlassenstufe() === "EINS" || this.#bwertungsbogenZWEISubmitted && this.#getTheKlassenstufe() === "ZWEI") {
				this.showFormular = false;
			}
		});
	}

	ngOnDestroy(): void {

		this.#bewertungCombinedSubscription.unsubscribe();

	}

	getImageSource(itemValue: BewertungAufgabeGUIModel): string {
		return itemValue.imageBase64;
	}

	onSubmit(): void {
		const theBewertungsbogen: BewertungsbogenKlassenstufe = mapFormValueToBewertungsbogen(this.#getTheKlassenstufe(), this.form.value);
		// console.log(JSON.stringify(theBewertungsbogen));
		this.feedbackFacade.saveBewertungsbogen(theBewertungsbogen)

		console.log('jetzt absenden')
	}

	onCancel(): void {
		// const klassenstufe: Klassenstufenart = #getTheKlassenstufe();
		// this.feedbackFacade.loadAufgabenvorschau(klassenstufe);
		this.#router.navigateByUrl('feedback/wettbewerb');
	}

	submitDisabled(): boolean {

		if (this.#bewertungSubmitted) {
			return true;
		}

		const theBewertungsbogen: BewertungsbogenKlassenstufe = mapFormValueToBewertungsbogen(this.#getTheKlassenstufe(), this.form.value);

		if (theBewertungsbogen.klassenstufe === "EINS" && this.#bewertungsbogenEINSSubmitted) {
			return true;
		}

		if (theBewertungsbogen.klassenstufe === "ZWEI" && this.#bwertungsbogenZWEISubmitted) {
			return true;
		}

		if (isBewertungsbogenLeer(theBewertungsbogen)) {
			return true;
		}

		return false;
	}

	#initForm(): void {
		this.#addItems(this.#bewertungsbogenGUIModel.items.length);
	}

	#addItems(count: number): void {
		const itemsArray = this.form.get('items') as FormArray;
		for (let i = 0; i < count; i++) {
			itemsArray.push(this.#createItemFormGroup(i))
		}
	}

	#createItemFormGroup(i: number) {
		const item: BewertungAufgabeGUIModel = this.#bewertungsbogenGUIModel.items[i];

		const formGroup = this.#fb.group({
			nummer: [''],
			aufgabeKategorie: [''],
			kategorie: [''],
			freitextAufgabe: [''],
			imageBase64: [''],
			lehrplan: ['0'],
			verstaendlichkeit: ['0'],
			schwierigkeitsgrad: ['0']
		});

		formGroup.patchValue({
			nummer: item.nummer,
			aufgabeKategorie: item.aufgabeKategorie,
			kategorie: item.empfohleneKategorie,
			freitextAufgabe: item.freitext,
			imageBase64: item.imageBase64,
			lehrplan: item.scoreLehrplankompatibilitaet + '',
			verstaendlichkeit: item.scoreVerstaendlichkeit + '',
			schwierigkeitsgrad: item.scoreSchwierigkeitsgrad + ''
		});

		return formGroup;
	}

	#getTheKlassenstufe(): Klassenstufenart {

		return getKlassenstufeByLabel(this.#bewertungsbogenGUIModel.klassenstufe).klassenstufe;
	}
}
