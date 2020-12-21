import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StoreModule } from '@ngrx/store';
import * as fromLoesungszettel from './+state/loesungszettel.reducer';
import { LoesungszettelEditorComponent } from './loesungszettel-editor/loesungszettel-editor.component';
import { LoesungszettelEditorResolver } from './loesungszettel-editor/loesungszettel-editor.resolver';
import { LoesungszettelRoutingModule } from './loesungszettel-routing.module';



@NgModule({
	declarations: [
		LoesungszettelEditorComponent
	],
	imports: [
		CommonModule,
		NgbModule,
		LoesungszettelRoutingModule,
		StoreModule.forFeature(fromLoesungszettel.loesungszettelFeatureKey, fromLoesungszettel.reducer)
	],
	providers: [
		LoesungszettelEditorResolver
	]
})
export class LoesungszettelModule {

	constructor() {
		console.log('LoesungszettelModule created')
	}
}
