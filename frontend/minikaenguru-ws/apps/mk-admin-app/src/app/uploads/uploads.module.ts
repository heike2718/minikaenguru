import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UploadsListComponent } from './uploads-list/uploads-list.component';
import * as fromUploads from './+state/uploads.reducer';
import { StoreModule } from '@ngrx/store';
import { UploadsCardComponent } from './uploads-card/uploads-card.component';
import { UploadsRoutingModule } from './uploads-routing.module';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { SharedModule } from '../shared/shared.module';

@NgModule({
  declarations: [
    UploadsListComponent, 
    UploadsCardComponent
  ],
  imports: [
    CommonModule,
    UploadsRoutingModule,
    CommonComponentsModule,
    SharedModule,
    StoreModule.forFeature(fromUploads.uploadsFeatureKey, fromUploads.reducer)    
  ]
})
export class UploadsModule { }
