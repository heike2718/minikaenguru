import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromKataloge from './reducers';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    StoreModule.forFeature(fromKataloge.katalogeFeatureKey, fromKataloge.reducers, { metaReducers: fromKataloge.metaReducers })
  ]
})
export class KatalogeModule { }
