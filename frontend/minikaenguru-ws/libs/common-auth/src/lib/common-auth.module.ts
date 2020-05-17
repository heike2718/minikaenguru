import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { CommonLoggingModule } from '@minikaenguru-ws/common-logging';
import { MkAuthConfig, MkAuthConfigService } from './configuration/mk-auth-config';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from './+state/auth.reducer';
import { AuthEffects } from './+state/auth.effects';
import { EffectsModule } from '@ngrx/effects';
import { AuthInterceptor } from './auth.interceptor';

@NgModule({
  imports: [
	  CommonModule,
	  HttpClientModule,
	  CommonMessagesModule,
	  CommonLoggingModule,
	  StoreModule.forFeature(fromAuth.authFeatureKey, fromAuth.reducer),
	  EffectsModule.forFeature([AuthEffects])
	]
})
export class CommonAuthModule {
	  static forRoot(config: MkAuthConfig): ModuleWithProviders<CommonAuthModule> {
    return {
      ngModule: CommonAuthModule,
      providers: [
        {
          provide: MkAuthConfigService,
          useValue: config
		},
		{
			provide: HTTP_INTERCEPTORS,
			useClass: AuthInterceptor,
			multi: true
		},
      ]
    }
  }
}
