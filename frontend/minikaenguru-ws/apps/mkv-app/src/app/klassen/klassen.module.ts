import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KlassenListComponent } from './klassen-list/klassen-list.component';
import { KlasseDetailsComponent } from './klasse-details/klasse-details.component';
import { KlassenRoutingModule } from './klassen-routing.module';
import { StoreModule } from '@ngrx/store';
import * as fromKlassen from './+state/klassen.reducer';
import { KlasseEditorComponent } from './klasse-editor/klasse-editor.component';
import { KlasseEditorResolver } from './klasse-editor/klasse-editor.resolver';
import { FormsModule } from '@angular/forms';



@NgModule({
	declarations: [
		KlassenListComponent,
		KlasseDetailsComponent,
		KlasseEditorComponent
	],
	imports: [
		CommonModule,
		FormsModule,
		KlassenRoutingModule,
		StoreModule.forFeature(fromKlassen.klassenFeatureKey, fromKlassen.reducer)
	],
	providers: [
		KlasseEditorResolver
	]
})
export class KlassenModule { }
