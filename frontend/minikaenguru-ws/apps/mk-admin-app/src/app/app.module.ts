import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, ErrorHandler, LOCALE_ID } from '@angular/core';
import { EffectsModule } from '@ngrx/effects';

import { registerLocaleData } from '@angular/common';
import localeDe from '@angular/common/locales/de';


import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { StoreRouterConnectingModule, RouterState, RouterStateSerializer } from "@ngrx/router-store";
import { environment } from '../environments/environment';

import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { CommonAuthModule } from '@minikaenguru-ws/common-auth';
import { CommonLoggingModule } from '@minikaenguru-ws/common-logging';
import { CommonComponentsModule } from '@minikaenguru-ws/common-components';
import { AdminSchulkatalogModule } from '@minikaenguru-ws/admin-schulkatalog'

import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { NavbarComponent } from './navbar/navbar.component';
import { CustomRouterStateSerializer } from './shared/utils';
import { GlobalErrorHandlerService } from './infrastructure/global-error-handler.service';
import { NotAuthorizedComponent } from './not-authorized/not-authorized.component';
import { WettbewerbeModule } from './wettbewerbe/wettbewerbe.module';
import { HttpClientModule } from '@angular/common/http';
import { DashboardComponent } from './dashboard/dashboard.component';
import { ReactiveFormsModule } from '@angular/forms';
import { AktuelleMeldungModule } from './aktuelle-meldung/aktuelle-meldung.module';
import { VeranstalterModule } from './veranstalter/veranstalter.module';
import { LayouttestsModule } from './layouttests/layouttests.module';
import { SchulteilnahmenModule } from './schulteilnahmen/schulteilnahmen.module';
import { EventlogModule } from './eventlog/eventlog.module';
import { SessionTimeoutComponent } from './session-timeout/session-timeout.component';
import { MustertexteModule } from './mustertexte/mustertexte.module';
import { NewsletterModule } from './newsletter/newsletter.module';
import { StoreDevModules } from './store-config/store-devtools';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { UploadsModule } from './uploads/uploads.module';
import { LoesungszettelModule } from './loesungszettel/loesungszettel.module';
import { SharedModule } from './shared/shared.module';
import { StatistikModule } from './statistik/statistik.module';
import { VersandauftraegeModule } from './versandauftraege/versandauftraege.module';

registerLocaleData(localeDe);


@NgModule({
	declarations: [
		AppComponent,
		NotFoundComponent,
		LandingComponent,
		NavbarComponent,
		DashboardComponent,
		NotAuthorizedComponent,
		SessionTimeoutComponent,
		],
	imports: [
		BrowserModule,
		ReactiveFormsModule,
		HttpClientModule,
		BrowserAnimationsModule,
		NgbModule,
		CommonMessagesModule,
		CommonComponentsModule.forRoot({
			baseUrl: environment.apiUrl
		}) ,
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
			profileUrl: environment.profileUrl
		}),
		AdminSchulkatalogModule,
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
		StoreDevModules,
		AktuelleMeldungModule,
		WettbewerbeModule,
		AdminSchulkatalogModule.forRoot({
			baseUrl: environment.apiUrl,
			devmode: environment.envName === 'DEV'
		}),
		VeranstalterModule,
		SchulteilnahmenModule,
		EventlogModule,
		MustertexteModule,
		NewsletterModule,
		VersandauftraegeModule,
		UploadsModule,
		LoesungszettelModule,
		StatistikModule,
		LayouttestsModule,
		SharedModule,		
		AppRoutingModule, // <-- immer am Ende, damit die wildcard-route als letzte deklariert bleibt
	],
	providers: [
		GlobalErrorHandlerService,
		{ provide: ErrorHandler, useClass: GlobalErrorHandlerService },
		{ provide: RouterStateSerializer, useClass: CustomRouterStateSerializer },
		{ provide: LOCALE_ID, useValue: "de-DE" },
	],
	bootstrap: [AppComponent]
})
export class AppModule {


 }
