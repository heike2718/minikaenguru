import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { StoreModule } from '@ngrx/store';
import * as fromLoesungszettel from './+state/loesungszettel.reducer';
import { LoesungszettelEditorComponent } from './loesungszettel-editor/loesungszettel-editor.component';
import { LoesungszettelRoutingModule } from './loesungszettel-routing.module';
import { CheckboxComponent } from './checkbox/checkbox.component';
import { LoesungszettelrowComponent } from './loesungszettelrow/loesungszettelrow.component';
import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';



@NgModule({
	declarations: [
		LoesungszettelEditorComponent,
		CheckboxComponent,
		LoesungszettelrowComponent
	],
	imports: [
		CommonModule,
		NgbModule,
		CommonMessagesModule,
		LoesungszettelRoutingModule,
		StoreModule.forFeature(fromLoesungszettel.loesungszettelFeatureKey, fromLoesungszettel.reducer)
	]
})
export class LoesungszettelModule {

	constructor() {
		console.log('LoesungszettelModule created')
	}
}
