import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, ErrorHandler } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { RouterStateSerializer, StoreRouterConnectingModule, RouterState, routerReducer } from "@ngrx/router-store";
import { environment } from '../environments/environment';

import { CommonSchulkatalogModule } from '@minikaenguru-ws/common-schulkatalog';
import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { CommonAuthModule } from '@minikaenguru-ws/common-auth';
import { CommonLoggingModule } from '@minikaenguru-ws/common-logging';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';

import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { NavbarComponent } from './navbar/navbar.component';
import { CustomRouterStateSerializer } from './shared/utils';
import { GlobalErrorHandlerService } from './infrastructure/global-error-handler.service';
import { DashboardComponent } from './dashboard/dashboard.component';
import { NotAuthorizedComponent } from './not-authorized/not-authorized.component';


@NgModule({
	declarations: [
		AppComponent,
		NotFoundComponent,
		LandingComponent,
		NavbarComponent,
		DashboardComponent,
		NotAuthorizedComponent],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		CommonSchulkatalogModule.forRoot({
			baseUrl: environment.katalogApiUrl,
			devmode: !environment.production,
			admin: false
		}),
		CommonMessagesModule,
		CommonComponentsModule,
		CommonLoggingModule.forRoot({
			consoleLogActive: environment.consoleLogActive,
			serverLogActive: environment.serverLogActive,
			loglevel: environment.loglevel
		}),
		CommonAuthModule.forRoot({
			baseUrl: environment.apiUrl,
			production: environment.production,
			storagePrefix: environment.storageKeyPrefix,
			loginSuccessUrl: '/dashboard'
		}),
		StoreModule.forRoot(reducers, {
			metaReducers,
			runtimeChecks: {
				strictStateImmutability: true,
				strictActionImmutability: true,
				strictActionSerializability: true,
				strictStateSerializability: true,
				strictActionWithinNgZone: true
			}
		}),
		EffectsModule.forRoot([]),
		StoreRouterConnectingModule.forRoot({
			stateKey: 'router',
			routerState: RouterState.Minimal
		}),
		StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: environment.production }),
		AppRoutingModule, // <-- immer am Ende, damit die wildcard-route als letzte deklariert bleibt
	],
	providers: [
		GlobalErrorHandlerService,
		{ provide: ErrorHandler, useClass: GlobalErrorHandlerService },
		{ provide: RouterStateSerializer, useClass: CustomRouterStateSerializer }
	],
	bootstrap: [AppComponent]
})
export class AppModule { }
