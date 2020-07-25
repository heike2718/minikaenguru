import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DialogComponent } from './dialog/dialog.component';
import { FormErrorComponent } from './form-error/form-error.component';
import { UploadComponent } from './upload/upload.component';
import { HttpClientModule } from '@angular/common/http';
import { MkComponentsConfig, MkComponentsConfigService } from './configuration/mk-components-config';
import { UploadService } from './upload/upload.service';

@NgModule({
	imports: [
		CommonModule,
		HttpClientModule
	],
	declarations: [
		DialogComponent,
		FormErrorComponent,
		UploadComponent
	],
	exports: [
		DialogComponent,
		FormErrorComponent,
		UploadComponent
	]
})
export class CommonComponentsModule {

	static forRoot(config: MkComponentsConfig): ModuleWithProviders<CommonComponentsModule> {


		return {
			ngModule: CommonComponentsModule,
			providers: [
				{
					provide: MkComponentsConfigService,
					useValue: config
				},
				{
					provide: UploadService
				}

			]
		}
	}
}
