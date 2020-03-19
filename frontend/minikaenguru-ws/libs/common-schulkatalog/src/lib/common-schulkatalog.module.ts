import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { KatalogItemsSucheComponent } from './katalog-items-suche/katalog-items-suche.component';
import { SchulkatalogConfig, SchulkatalogConfigService } from './configuration/schulkatalog-config';
import { KatalogRoutingModule } from './katalog-routing.module';
import { SchulkatalogComponent } from './schulkatalog/schulkatalog.component';
import { KatalogItemComponent } from './katalog-item/katalog-item.component';
import { StoreModule } from '@ngrx/store';
import * as fromSchulkatalog from './+state/schulkatalog.reducer';
import { EffectsModule } from '@ngrx/effects';
import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { CommonLoggingModule } from '@minikaenguru-ws/common-logging';
import { SchulkatalogEffects } from './+state/schulkatalog.effects';

@NgModule({
  imports: [
	CommonModule,
	FormsModule,
    HttpClientModule,
    KatalogRoutingModule,
    StoreModule.forFeature(fromSchulkatalog.schulkatalogFeatureKey, fromSchulkatalog.reducer),
	EffectsModule.forFeature([SchulkatalogEffects]),
    CommonMessagesModule,
    CommonLoggingModule
  ],
  declarations: [
    KatalogItemsSucheComponent,
    SchulkatalogComponent,
    KatalogItemComponent],
  exports: [
    SchulkatalogComponent
  ]
})
export class CommonSchulkatalogModule {

  static forRoot(config: SchulkatalogConfig): ModuleWithProviders<CommonSchulkatalogModule> {
    return {
      ngModule: CommonSchulkatalogModule,
      providers: [
        {
          provide: SchulkatalogConfigService,
          useValue: config
        }
      ]
    }
  }
}
