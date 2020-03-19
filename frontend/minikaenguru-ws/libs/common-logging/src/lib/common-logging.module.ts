import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { LogService } from './log.service';
import { LogPublisher } from './domain/log-publishers';
import { LoggingConfig, LoggingConfigService } from './configuration/logging-config';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule
  ]
})
export class CommonLoggingModule {


	static forRoot(config: LoggingConfig): ModuleWithProviders<CommonLoggingModule> {

		return {
			ngModule: CommonLoggingModule,
			providers: [
				{
					provide: LoggingConfigService,
					useValue: config
				}
			]
		}
	}
}
