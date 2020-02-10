import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromKatalog from './+state/katalog.reducer';
import { EffectsModule } from '@ngrx/effects';
import { KatalogEffects } from './+state/katalog.effects';

@NgModule({
  imports: [CommonModule, StoreModule.forFeature(fromKatalog.katalogFeatureKey, fromKatalog.reducer), EffectsModule.forFeature([KatalogEffects])]
})
export class SchulkatalogDomainModule {}
