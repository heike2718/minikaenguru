import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { StoreModule } from '@ngrx/store';
import * as fromAuth from './reducers';



@NgModule({
  declarations: [],
  imports: [
	CommonModule,
	StoreModule.forFeature(fromAuth.authFeatureKey, fromAuth.reducers)
  ]
})
export class AuthModule { }
