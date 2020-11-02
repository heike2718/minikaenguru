import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromKinder from './+state/kinder.reducer';
import { KinderRoutingModule } from './kinder-routing.module';
import { KindDetailsComponent } from './kind-details/kind-details.component';
import { KinderListComponent } from './kinder-list/kinder-list.component';
import { KinderListResolver } from './kinder-list/kinder-list.resolver';
import { KindEditorComponent } from './kind-editor/kind-editor.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { ReactiveFormsModule } from '@angular/forms';
import { KindEditorResolver } from './kind-editor/kind-editor.resolver';



@NgModule({
	declarations: [
		KindDetailsComponent,
		KinderListComponent,
		KindEditorComponent
	],
	imports: [
		CommonModule,
		ReactiveFormsModule,
		KinderRoutingModule,
		NgbModule,
		StoreModule.forFeature(fromKinder.kinderFeatureKey, fromKinder.reducer),
	],
	providers: [
		KinderListResolver,
		KindEditorResolver
	]
})
export class KinderModule { }
