import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromPrivatauswertung from './+state/privatauswertung.reducer';
import { PrivatauswertungRoutingModule } from './privatauswertung-routing.module';
import { KindDetailsComponent } from './kind-details/kind-details.component';
import { KinderListComponent } from './kinder-list/kinder-list.component';
import { KinderListResolver } from './kinder-list/kinder-list.resolver';



@NgModule({
	declarations: [
		KindDetailsComponent,
		KinderListComponent
	],
	imports: [
		CommonModule,
		StoreModule.forFeature(fromPrivatauswertung.privatauswertungFeatureKey, fromPrivatauswertung.reducer),
		PrivatauswertungRoutingModule
	],
	providers: [
		KinderListResolver
	]
})
export class PrivatauswertungModule { }
