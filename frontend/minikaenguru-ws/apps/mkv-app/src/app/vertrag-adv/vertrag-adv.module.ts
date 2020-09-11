import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VertragAdvComponent } from './vertrag-adv.component';
import { StoreModule } from '@ngrx/store';
import * as fromVertragAdv from './+state/vertrag-adv.reducer';
import { VertragAdvRoutingModule } from './vertrag-adv-routing.module';
import { VertragAdvResolver } from './vertrag-avd.resolver';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';



@NgModule({
	imports: [
		CommonModule,
		CommonComponentsModule,
		ReactiveFormsModule,
		StoreModule.forFeature(fromVertragAdv.vertragAdvFeatureKey, fromVertragAdv.reducer),
		VertragAdvRoutingModule
	],
	declarations: [
		VertragAdvComponent
	],
	providers: [
		VertragAdvResolver
	]

})
export class VertragAdvModule { }
