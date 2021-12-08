import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { LoesungszettelListComponent } from './loesungszettel-list/loesungszettel-list.component';

@NgModule({
    declarations: [
        LoesungszettelListComponent
    ],
    imports: [
        CommonModule,
        CommonComponentsModule
    ]
  })
export class LoesungszettelModule { }