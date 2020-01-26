import { BrowserModule } from '@angular/platform-browser';
import { NgModule, ErrorHandler } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { MkNgCommonsModule } from '../../../mk-ng-commons/projects/mk-ng-commons/src/lib/mk-ng-commons.module';
import { HewiNgLibModule } from 'hewi-ng-lib';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { AuthModule } from './auth/auth.module';
import { KatalogeModule } from './kataloge/kataloge.module';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { GlobalErorHandlerService } from './error/global-error-handler.service';

@NgModule({
  declarations: [
	AppComponent,
	NavbarComponent,
  ],
  imports: [
  BrowserModule,
	AppRoutingModule,
	HttpClientModule,
	HewiNgLibModule,
	MkNgCommonsModule,
	AuthModule,
	KatalogeModule,
	StoreModule.forRoot(reducers, {
		metaReducers,
		runtimeChecks: {
		strictStateImmutability: true,
		strictActionImmutability: true,
		strictActionSerializability: true,
		strictStateSerializability: true
		}
	}),
	!environment.production ? StoreDevtoolsModule.instrument() : [],
  ],
  providers: [
		GlobalErorHandlerService, {provide: ErrorHandler, useClass: GlobalErorHandlerService }
	],
  bootstrap: [AppComponent]
})
export class AppModule { }
