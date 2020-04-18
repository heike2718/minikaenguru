import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import * as fromRegistration from './+state/registration.reducer';
import { StoreModule } from '@ngrx/store';
import { RegistrationComponent } from './registration.component';
import { CommonSchulkatalogModule } from '@minikaenguru-ws/common-schulkatalog';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';


@NgModule({
	imports: [
		CommonModule,
		FormsModule,
		HttpClientModule,
		CommonSchulkatalogModule,
		CommonComponentsModule,
		StoreModule.forFeature(fromRegistration.registrationFeatureKey, fromRegistration.reducer),
	],
	declarations: [
		RegistrationComponent
	],
	exports: [
		RegistrationComponent
	]
})
export class RegistrationModule {

}
