import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { CommonLoggingModule } from '@minikaenguru-ws/common-logging';
import { MkvAuthConfig, MkvAuthConfigService } from './configuration/mkv-auth-config';

@NgModule({
  imports: [
	  CommonModule,
	  HttpClientModule,
	  CommonMessagesModule,
	  CommonLoggingModule
	]
})
export class CommonAuthModule {
	  static forRoot(config: MkvAuthConfig): ModuleWithProviders<CommonAuthModule> {
    return {
      ngModule: CommonAuthModule,
      providers: [
        {
          provide: MkvAuthConfigService,
          useValue: config
        }
      ]
    }
  }
}
