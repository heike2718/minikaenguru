import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth.service';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from './auth.reducers';
import { AuthGuard } from './auth.guard';
import { EffectsModule } from '@ngrx/effects';
import { AuthEffects } from './auth.effects';



@NgModule({
	declarations: [],
	imports: [
		CommonModule,
		StoreModule.forFeature(fromAuth.authFeatureKey, fromAuth.authReducerWrapper),   // important; use the wrapper function here!
		EffectsModule.forFeature([AuthEffects])
	]
})

export class AuthModule {
	static forRoot(): ModuleWithProviders {
		return {
			ngModule: AuthModule,
			providers: [
				AuthService,
				AuthGuard
			]
		};
	}
}
