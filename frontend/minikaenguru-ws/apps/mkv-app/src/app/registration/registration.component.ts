import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { SchulkatalogFacade, KatalogItem } from '@minikaenguru-ws/common-schulkatalog';
import { Subscription } from 'rxjs';
import { Store } from '@ngrx/store';
import { RegistrationState } from './+state/registration.reducer';
import { selectSubmitStatus
	, selectRegistrationMode
	, selectShowSchulkatalog
	, selectShowSuccessDialog
	, selectSuccessDialogContent } from './+state/registration.selectors';
import { AuthService } from '@minikaenguru-ws/common-auth';
import { DialogService } from '@minikaenguru-ws/common-components';
import * as RegistrationActions from './+state/registration.actions';

@Component({
	selector: 'mkv-registration',
	templateUrl: './registration.component.html',
	styleUrls: ['./registration.component.css']
})
export class RegistrationComponent implements OnInit, OnDestroy {

	devMode: boolean;

	private selectedKatalogItem: KatalogItem;

	pfadKatalogItem: string;

	showSchulkatalog$ = this.store.select(selectShowSchulkatalog);

	showSuccessDialog$ = this.store.select(selectShowSuccessDialog);

	registrationSuccessMessage$ = this.store.select(selectSuccessDialogContent);

	registrationMode: string;

	submitDisabled: boolean;

	private selectedKatalogItemSubskription: Subscription;

	private submitEnabledSubscription: Subscription;

	private registrationModeSubscription: Subscription;

	constructor(private router: Router
		, public schulkatalogFacade: SchulkatalogFacade
		, private authService: AuthService
		, private dialogService: DialogService
		, private store: Store<RegistrationState>) {

		this.devMode = !environment.production;

	}

	ngOnInit() {

		this.initState();

		this.store.dispatch(RegistrationActions.resetRegistrationState());
		this.schulkatalogFacade.initSchulkatalog('ORT');

		this.selectedKatalogItemSubskription = this.schulkatalogFacade.selectedKatalogItem$.subscribe(
			item => {
				if (item) {
					this.selectedKatalogItem = item;
					this.pfadKatalogItem = item.pfad;
					if (item.typ === 'SCHULE') {
						this.store.dispatch(RegistrationActions.schuleSelected({ schulkuerzel: item.kuerzel }));
					}
				}
			}
		);

		this.submitEnabledSubscription = this.store.select(selectSubmitStatus).subscribe(
			state => this.submitDisabled = !state
		)

		this.registrationModeSubscription = this.store.select(selectRegistrationMode).subscribe(
			mode => this.registrationMode = mode
		);
	}

	ngOnDestroy() {

		if (this.selectedKatalogItemSubskription) {
			this.selectedKatalogItemSubskription.unsubscribe();
		}
		if (this.submitEnabledSubscription) {
			this.submitEnabledSubscription.unsubscribe();
		}
		if (this.registrationModeSubscription) {
			this.registrationModeSubscription.unsubscribe();
		}

		this.initState();

	}

	gotoKatalogsuche() {
		this.router.navigate([]);
	}

	modusLehrerkonto() {
		this.store.dispatch(RegistrationActions.registrationModeChanged({ mode: 'LEHRER' }));
		this.schulkatalogFacade.initSchulkatalog('ORT');
	}

	neueSchulsuche() {
		this.schulkatalogFacade.initSchulkatalog('ORT');
		this.store.dispatch(RegistrationActions.resetSchulsuche());
	}

	privatkontoAnlegen() {
		this.store.dispatch(RegistrationActions.registrationModeChanged({mode: 'PRIVAT'}));
		this.authService.privatkontoAnlegen();
	}

	lehrerkontoAnlegen() {

		const schulkuerzel = this.selectedKatalogItem.kuerzel;
		this.authService.lehrerkontoAnlegen(schulkuerzel)
	}

	closeDialog() {
		this.dialogService.close();
		this.initState();
		this.router.navigateByUrl('/');
	}

	cancel() {
		this.router.navigateByUrl('/');
	}

	private initState() {
		this.store.dispatch(RegistrationActions.resetRegistrationState());
		this.schulkatalogFacade.initSchulkatalog('ORT');
	}

}
