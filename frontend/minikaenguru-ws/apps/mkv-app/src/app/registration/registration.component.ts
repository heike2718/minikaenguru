import { Component, OnInit, OnDestroy, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { SchulkatalogFacade, KatalogItem } from '@minikaenguru-ws/common-schulkatalog';
import { Subscription } from 'rxjs';
import { Store } from '@ngrx/store';
import { RegistrationState } from './+state/registration.reducer';
import { selectSubmitStatus, selectRegistrationMode, selectShowShulkatalog } from './+state/registration.selectors';
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

	showShulkatalog$ = this.store.select(selectShowShulkatalog);

	registrationMode: string;

	submitDisabled: boolean;

	private selectedKatalogItemSubskription: Subscription;

	private submitEnabledSubscription: Subscription;

	private registrationModeSubscription: Subscription;

	constructor(private router: Router, public schulkatalogFacade: SchulkatalogFacade, private store: Store<RegistrationState>) {

		this.devMode = !environment.production;

	}

	ngOnInit() {

		this.store.dispatch(RegistrationActions.resetRegistrationState());

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

		this.store.dispatch(RegistrationActions.resetRegistrationState());

	}

	gotoKatalogsuche() {
		this.router.navigate([]);
	}

	modusLehrerkonto() {
		this.store.dispatch(RegistrationActions.registrationModeChanged({ mode: 'LEHRER' }));
	}

	privatkontoAnlegen() {
		this.store.dispatch(RegistrationActions.registrationModeChanged({mode: 'PRIVAT'}));

	}

	lehrerkontoAnlegen() {

	}

	cancel() {
		this.router.navigateByUrl('/');
	}

}
