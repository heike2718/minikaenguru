import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MustertextListComponent } from './mustertext-list/mustertext-list.component';
import { MustertexteRoutingModule } from './mustertexte-routing.module';
import { StoreModule } from '@ngrx/store';
import * as fromMustertexte from './+state/mustertexte.reducer';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MustertextCardComponent } from './mustertext-card/mustertext-card.component';
import { EditMustertextComponent } from './edit-mustertext/edit-mustertext.component';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { SendMailComponent } from './send-mail/send-mail.component';



@NgModule({
  declarations: [
    MustertextListComponent,
    MustertextCardComponent,
    EditMustertextComponent,
    SendMailComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    CommonComponentsModule,
    MustertexteRoutingModule,
    StoreModule.forFeature(fromMustertexte.mustertexteFeatureKey, fromMustertexte.reducer)
  ]
})
export class MustertexteModule { }
