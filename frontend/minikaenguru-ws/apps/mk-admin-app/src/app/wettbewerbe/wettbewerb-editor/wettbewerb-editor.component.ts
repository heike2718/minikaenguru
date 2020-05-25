import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, Validators, FormGroup, FormControl } from '@angular/forms';
import { environment } from '../../../environments/environment';
import { Store } from '@ngrx/store';
import { AppState } from '../../reducers';
import { selectedWettbewerb } from '../+state/wettbewerbe.selectors';
import { Subscription } from 'rxjs';
import { WettbewerbEditorModel, Wettbewerb } from '../wettbewerbe.model';

@Component({
	selector: 'mka-wettbewerb-editor',
	templateUrl: './wettbewerb-editor.component.html',
	styleUrls: ['./wettbewerb-editor.component.css']
})
export class WettbewerbEditorComponent implements OnInit, OnDestroy {

	devMode = !environment.production;

	wettbewerbForm: FormGroup;

	wettbewerb$ = this.store.select(selectedWettbewerb);

	private wettbewerbSubscription: Subscription;

	private wettbewerbGuiModel = {} as WettbewerbEditorModel;

	private wettbewerb: Wettbewerb;

	constructor(private fb: FormBuilder, private store: Store<AppState>) { }

	jahrFormControl: FormControl;

	statusFormControl: FormControl;

	wettbewerbsbeginnFormControl: FormControl;

	wettbewerbsendeFormControl: FormControl;

	datumFreischaltungLehrerFormControl: FormControl;

	datumFreischaltungPrivatFormControl: FormControl;

	ngOnInit(): void {

		this.wettbewerbSubscription = this.wettbewerb$.subscribe(
			wb => {
				this.wettbewerb = wb;
				this.initGuiModel(wb);

				this.jahrFormControl = new FormControl({value: ''}, Validators.required);
				if (this.wettbewerb.jahr === 0) {

					this.statusFormControl = new FormControl({ value: '', disabled: true }, Validators.required);
				} else {
					this.statusFormControl = new FormControl({ value: '', disabled: false }, Validators.required);
				}

				this.wettbewerbsbeginnFormControl = new FormControl({value: ''});
				this.wettbewerbsendeFormControl = new FormControl({value: ''}, Validators.required);
				this.datumFreischaltungLehrerFormControl = new FormControl({value: ''}, Validators.required);
				this.datumFreischaltungPrivatFormControl = new FormControl({value: ''}, Validators.required);


				this.wettbewerbForm = this.fb.group({
					jahr: this.jahrFormControl,
					status: this.statusFormControl,
					wettbewerbsbeginn: this.wettbewerbsbeginnFormControl,
					wettbewerbsende: this.wettbewerbsendeFormControl,
					datumFreischaltungLehrer: this.datumFreischaltungLehrerFormControl,
					datumFreischaltungPrivat: this.datumFreischaltungPrivatFormControl
				});


				this.wettbewerbForm.patchValue(this.wettbewerbGuiModel);
			}
		)
	}

	private initGuiModel(wb: Wettbewerb): void {
		this.wettbewerbGuiModel.jahr = wb.jahr;
		this.wettbewerbGuiModel.status = wb.status;
		if (wb.wettbewerbsbeginn) {
			this.wettbewerbGuiModel.wettbewerbsbeginn = wb.wettbewerbsbeginn;

		} else {
			this.wettbewerbGuiModel.wettbewerbsbeginn = '';
		}

		if (wb.wettbewerbsende) {
			this.wettbewerbGuiModel.wettbewerbsende = wb.wettbewerbsende;
		} else {

			this.wettbewerbGuiModel.wettbewerbsende = '';
		}

		if (wb.datumFreischaltungLehrer) {
			this.wettbewerbGuiModel.datumFreischaltungLehrer = wb.datumFreischaltungLehrer;
		} else {
			this.wettbewerbGuiModel.datumFreischaltungLehrer = '';
		}

		if (wb.datumFreischaltungPrivat) {
			this.wettbewerbGuiModel.datumFreischaltungPrivat = wb.datumFreischaltungPrivat;
		} else {
			this.wettbewerbGuiModel.datumFreischaltungPrivat = '';
		}
	}

	ngOnDestroy(): void {
		if (this.wettbewerbSubscription) {
			this.wettbewerbSubscription.unsubscribe();
		}
	}

	submitDisabled(): boolean {
		return this.wettbewerbForm.invalid;
	}

	onSubmit() {
		// TODO: Use EventEmitter with form value
		console.warn(this.wettbewerbForm.value);
	}

	onCancel() {
		this.initGuiModel(this.wettbewerb);
		this.wettbewerbForm.patchValue(this.wettbewerbGuiModel);
	}
}
