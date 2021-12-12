import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { LoesungszettelListComponent } from './loesungszettel-list/loesungszettel-list.component';
import { LoesungszettelRoutingModule } from './loesungszettel-routing.module';
import * as fromLoesungszettel from './+state/loesungszettel.reducer';
import { LoesungszettelCardComponent } from './loesungszettel-card/loesungszettel-card.component';
import { FormsModule } from '@angular/forms';

@NgModule({
    declarations: [
        LoesungszettelListComponent,
        LoesungszettelCardComponent
    ],
    imports: [
        CommonModule,
        FormsModule,
        CommonComponentsModule,
        LoesungszettelRoutingModule,
        StoreModule.forFeature(fromLoesungszettel.loesungszettelFeatureKey, fromLoesungszettel.reducer)
    ]
  })
export class LoesungszettelModule { }