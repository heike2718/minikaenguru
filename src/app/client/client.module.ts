import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromClient from './client.reducer';
import { EffectsModule } from '@ngrx/effects';
import { ClientEffects } from './client.effects';
import { ClientService } from './client.service';



@NgModule({
	declarations: [],
	imports: [
		CommonModule,
		StoreModule.forFeature(fromClient.clientFeatureKey, fromClient.clientReducer),
		EffectsModule.forFeature([ClientEffects])
	]
})
export class ClientModule {
	static forRoot(): ModuleWithProviders {
		return {
			ngModule: ClientModule,
			providers: [
				ClientService
			]
		};
	}
}
