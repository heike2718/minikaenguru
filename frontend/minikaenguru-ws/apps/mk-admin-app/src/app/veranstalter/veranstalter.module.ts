import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VeranstalterListComponent } from './veranstalter-list/veranstalter-list.component';
import { FormsModule } from '@angular/forms';
import { StoreModule } from '@ngrx/store';
import { VeranstalterRoutingModule } from './veranstalter-routing.module';
import * as fromVeranstalter from './+state/veranstalter.reducer';
import { VeranstalterCardComponent } from './veranstalter-card/veranstalter-card.component';



@NgModule({
	declarations: [
		VeranstalterListComponent,
		VeranstalterCardComponent
	],
	imports: [
		CommonModule,
		FormsModule,
		VeranstalterRoutingModule,
		StoreModule.forFeature(fromVeranstalter.veranstalterFeatureKey, fromVeranstalter.reducer)
	]
})
export class VeranstalterModule { }
