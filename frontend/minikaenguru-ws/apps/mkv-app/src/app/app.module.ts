import { BrowserModule } from '@angular/platform-browser';
import { NgModule, ErrorHandler } from '@angular/core';

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
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { RouterStateSerializer, StoreRouterConnectingModule, RouterState, routerReducer} from "@ngrx/router-store";
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



@NgModule({
	declarations: [
		AppComponent,
		NavbarComponent,
		NotFoundComponent,
		LandingComponent,
		DashboardComponent
	],
	imports: [
		BrowserModule,
		CommonSchulkatalogModule.forRoot({
			baseUrl: environment.apiUrl,
			devmode: !environment.production,
			admin: false,
			immediatelyLoadOnNumberChilds: 25,
			cancelKatalogantragRedirectPath: '/landing'
		}),
		CommonMessagesModule,
		CommonComponentsModule,
		CommonLoggingModule.forRoot({
			consoleLogActive: environment.consoleLogActive,
			serverLogActive: environment.serverLogActive,
			loglevel: environment.loglevel
		}),
		CommonAuthModule.forRoot({
			baseUrl: environment.apiUrl + '/veranstalter',
			production: environment.production,
			storagePrefix: environment.storageKeyPrefix,
			loginSuccessUrl: '/dashboard'
		}),
		AktuelleMeldungModule,
		WettbewerbModule,
		RegistrationModule,
		LehrerModule,
		PrivatveranstalterModule,
		TeilnahmenModule,
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
