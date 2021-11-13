import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { KlassenListComponent } from './klassen-list/klassen-list.component';
import { KlasseDetailsComponent } from './klasse-details/klasse-details.component';
import { KlassenRoutingModule } from './klassen-routing.module';
import { StoreModule } from '@ngrx/store';
import * as fromKlassen from './+state/klassen.reducer';
import { KlasseEditorComponent } from './klasse-editor/klasse-editor.component';
import { FormsModule } from '@angular/forms';
import { KlasseNameValidatorDirective } from './klasse-editor/klasse-name-validator.directive';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { UploadKlassenlistenComponent } from './upload-klassenlisten/upload-klassenlisten.component';
import { ImportReportComponent } from './import-report/import-report.component';
import { ImportFehlerComponent } from './import-fehler/import-fehler.component';



@NgModule({
	declarations: [
		KlassenListComponent,
		KlasseDetailsComponent,
		KlasseEditorComponent,
		KlasseNameValidatorDirective,
  		UploadKlassenlistenComponent,
  		ImportReportComponent,
  		ImportFehlerComponent
	],
	imports: [
		CommonModule,
		CommonComponentsModule,
		FormsModule,
		NgbModule,
		KlassenRoutingModule,
		StoreModule.forFeature(fromKlassen.klassenFeatureKey, fromKlassen.reducer)
	]
})
export class KlassenModule { }
