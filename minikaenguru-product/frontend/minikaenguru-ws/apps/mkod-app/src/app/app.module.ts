import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, ErrorHandler, LOCALE_ID } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';

import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';

import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { StoreModule } from '@ngrx/store';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { reducers, metaReducers } from './reducers';
import { StoreRouterConnectingModule, RouterState, RouterStateSerializer } from "@ngrx/router-store";
import { environment } from '../environments/environment';
import { CustomRouterStateSerializer } from './shared/utils';

import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { CommonAuthModule } from '@minikaenguru-ws/common-auth';
import { CommonLoggingModule } from '@minikaenguru-ws/common-logging';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';

import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { NavbarComponent } from './navbar/navbar.component';
import { GlobalErrorHandlerService } from './infrastructure/global-error-handler.service';
import { AnmeldungenModule } from './anmeldungen/anmeldungen.module';
import { StoreDevModules } from './store-config/store-devtools';
import { TeilnahmenModule } from './teilnahmen/teilnahmen.module';
import { WettbewerbeModule } from './wettbewerbe/wettbewerbe.module';

registerLocaleData(localeDe);


@NgModule({
	declarations: [
		AppComponent,
		NotFoundComponent,
		LandingComponent,
		NavbarComponent,
	],
	imports: [
		BrowserModule,
		ReactiveFormsModule,
		HttpClientModule,
		BrowserAnimationsModule,
		CommonMessagesModule,
		CommonComponentsModule.forRoot({
			baseUrl: environment.apiUrl
		}),
		CommonLoggingModule.forRoot({
			consoleLogActive: environment.consoleLogActive,
			serverLogActive: environment.serverLogActive,
			loglevel: environment.loglevel
		}),
		CommonAuthModule.forRoot({
			baseUrl: environment.apiUrl,
			production: environment.production,
			storagePrefix: environment.storageKeyPrefix,
			loginSuccessUrl: '/dashboard',
			profileUrl: ''
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
		AnmeldungenModule,
		TeilnahmenModule,
		WettbewerbeModule,
		EffectsModule.forRoot([]),
		StoreRouterConnectingModule.forRoot({
			stateKey: 'router',
			routerState: RouterState.Minimal
		}),
		StoreDevModules,
		AppRoutingModule, // <-- immer am Ende, damit die wildcard-route als letzte deklariert bleibt
	],
	providers: [
		GlobalErrorHandlerService,
		{ provide: ErrorHandler, useClass: GlobalErrorHandlerService },
		{ provide: RouterStateSerializer, useClass: CustomRouterStateSerializer},
		{ provide: LOCALE_ID, useValue: "de-DE" },
	],
	bootstrap: [AppComponent]
})
export class AppModule { }
