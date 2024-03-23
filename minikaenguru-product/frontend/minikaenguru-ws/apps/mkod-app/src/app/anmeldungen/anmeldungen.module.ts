import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AnmeldungenListComponent } from './anmeldungen-list/anmeldungen-list.component';
import { AnmeldungenRoutingModule } from './anmeldungen-routing.module';
import * as fromAnmeldungen from './+state/anmeldungen.reducer';
import { StoreModule } from '@ngrx/store';
import { AnmeldungsitemCardComponent } from './anmeldungsitem-card/anmeldungsitem-card.component';



@NgModule({
	declarations: [
		AnmeldungenListComponent,
		AnmeldungsitemCardComponent
	],
	imports: [
		CommonModule,
		AnmeldungenRoutingModule,
		StoreModule.forFeature(fromAnmeldungen.anmeldungenFeatureKey, fromAnmeldungen.reducer)
	],
	providers: [
	]
})
export class AnmeldungenModule { }
