import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormErrorComponent } from './form-error/form-error.component';
import { UploadComponent } from './upload/upload.component';
import { HttpClientModule } from '@angular/common/http';
import {
  MkComponentsConfig,
  MkComponentsConfigService,
} from './configuration/mk-components-config';
import { UploadService } from './upload/upload.service';
import { DownloadButtonComponent } from './download/download-button/download-button.component';
import { DownloadCardComponent } from './download/download-card/download-card.component';
import * as fromDownload from './download/+state/download.reducer';
import { StoreModule } from '@ngrx/store';
import { AnonymisierteTeilnahmeComponent } from './anonymisierte-teilnahme/anonymisierte-teilnahme.component';
import { UnterlagenComponent } from './unterlagen/unterlagen.component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SwitchComponent } from './switch/switch.component';
import { PaginationComponent } from './pagination/pagination.component';
import { VersionService } from './version/version.service';
import { LoadingIndicatorComponent } from './loading-indicator/loading-indicator.component';
import { SharedUtilMkModule } from '@minikaenguru-ws/shared/util-mk';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    NgbModule,
    SharedUtilMkModule,
    StoreModule.forFeature(
      fromDownload.downloadFeatureKey,
      fromDownload.reducer
    ),
  ],
  declarations: [
    DownloadButtonComponent,
    DownloadCardComponent,
    FormErrorComponent,
    UploadComponent,
    AnonymisierteTeilnahmeComponent,
    UnterlagenComponent,
    SwitchComponent,
    PaginationComponent,
    LoadingIndicatorComponent,
  ],
  exports: [
    ReactiveFormsModule,
    DownloadButtonComponent,
    DownloadCardComponent,
    FormErrorComponent,
    UploadComponent,
    AnonymisierteTeilnahmeComponent,
    UnterlagenComponent,
    SwitchComponent,
    PaginationComponent,
    LoadingIndicatorComponent
  ],
})
export class CommonComponentsModule {
  static forRoot(
    config: MkComponentsConfig
  ): ModuleWithProviders<CommonComponentsModule> {
    return {
      ngModule: CommonComponentsModule,
      providers: [
        {
          provide: MkComponentsConfigService,
          useValue: config,
        },
        {
          provide: UploadService,
        },
        {
          provide: VersionService,
        },
      ],
    };
  }
}
