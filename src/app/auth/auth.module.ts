import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthService } from './auth.service';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from './reducers';
import { AuthGuard } from './auth.guard';
import { EffectsModule } from '@ngrx/effects';
import { AuthEffects } from './auth.effects';



@NgModule({
	declarations: [],
	imports: [
		CommonModule,
		ReactiveFormsModule,
		StoreModule.forFeature(fromAuth.authFeatureKey, fromAuth.authReducer),
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
