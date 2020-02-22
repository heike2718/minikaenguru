import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { KatalogItemsSucheComponent } from './katalog-items-suche/katalog-items-suche.component';
import { SchulkatalogConfig, SchulkatalogConfigService } from './configuration/schulkatalog-config';
import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { KatalogRoutingModule } from './katalog-routing.module';
import { SchulkatalogComponent } from './schulkatalog/schulkatalog.component';
import { KatalogItemComponent } from './katalog-item/katalog-item.component';

@NgModule({
  imports: [
    CommonModule,
    HttpClientModule,
    CommonMessagesModule,
    KatalogRoutingModule
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

  static forRoot(config: SchulkatalogConfig): ModuleWithProviders {
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
