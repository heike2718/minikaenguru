import { BrowserModule } from '@angular/platform-browser';
import { NgModule, ErrorHandler, LOCALE_ID } from '@angular/core';

import { AppComponent } from './app.component';
import { NavbarComponent } from './navbar/navbar.component';
import { AppRoutingModule } from './app-routing.module';

import { CommonSchulkatalogModule } from '@minikaenguru-ws/common-schulkatalog';
import { CommonMessagesModule } from '@minikaenguru-ws/common-messages';
import { CommonAuthModule } from '@minikaenguru-ws/common-auth';
import { CommonLoggingModule } from '@minikaenguru-ws/common-logging';
import { CommonComponentsModule  } from '@minikaenguru-ws/common-components';

import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { RouterStateSerializer, StoreRouterConnectingModule, RouterState} from "@ngrx/router-store";
import { environment } from '../environments/environment';
import { NotFoundComponent } from './not-found/not-found.component';
import { LandingComponent } from './landing/landing.component';
import { GlobalErrorHandlerService } from './infrastructure/global-error-handler.service';
import { EffectsModule } from '@ngrx/effects';
import { CustomRouterStateSerializer } from './shared/utils';
import { RegistrationModule } from './registration/registration.module';
import { LehrerModule } from './lehrer/lehrer.module';
import { PrivatveranstalterModule } from './privatveranstalter/privatveranstalter.module';
import { WettbewerbModule } from './wettbewerb/wettbewerb.module';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AktuelleMeldungModule } from './aktuelle-meldung/aktuelle-meldung.module';
import { TeilnahmenModule } from './teilnahmen/teilnahmen.module';
import { VertragAdvModule } from './vertrag-adv/vertrag-adv.module';
import { KinderModule } from './kinder/kinder.module';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { SessionTimeoutComponent } from './session-timeout/session-timeout.component';
import { KlassenModule } from './klassen/klassen.module';
import { LoesungszettelModule } from './loesungszettel/loesungszettel.module';
import { UrkundenModule } from './urkunden/urkunden.module';
import { StoreDevModules } from './store-config/store-devtools';




@NgModule({
	declarations: [
		AppComponent,
		NavbarComponent,
		NotFoundComponent,
		LandingComponent,
		DashboardComponent,
		SessionTimeoutComponent
	],
	imports: [
		BrowserModule,
		NgbModule,
		CommonSchulkatalogModule.forRoot({
			baseUrl: environment.apiUrl,
			devmode: environment.envName === 'DEV',
			admin: false,
			immediatelyLoadOnNumberChilds: 25,
			cancelKatalogantragRedirectPath: '/landing'
		}),
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
			baseUrl: environment.apiUrl + '/veranstalter',
			production: environment.production,
			storagePrefix: environment.storageKeyPrefix,
			loginSuccessUrl: '/dashboard',
			profileUrl: environment.profileUrl
		}),
		AktuelleMeldungModule,
		WettbewerbModule,
		RegistrationModule,
		LehrerModule,
		PrivatveranstalterModule,
		KinderModule,
		KlassenModule,
		TeilnahmenModule,
		VertragAdvModule,
		LoesungszettelModule,
		UrkundenModule,
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
			stateKey:'router',
			routerState: RouterState.Minimal
		}),
		StoreDevModules,		
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
export class AppModule { }
