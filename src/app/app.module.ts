import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, ErrorHandler } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { HewiNgLibModule } from 'hewi-ng-lib';


import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/components/button/button';
import { MessagesModule } from 'primeng/messages';


import { AppComponent } from './app.component';
import { AboutComponent } from './about/about.component';
import { AuthModule } from './auth/auth.module';
import { NavigationComponent } from './navigation/navigation.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AppRoutingModule } from './app-routing.module';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { EffectsModule } from '@ngrx/effects';
import { environment } from '../environments/environment';
import { MessageService } from 'primeng/api';
import { GlobalErrorHandlerService } from './error/global-error-handler.service';
import { HomeComponent } from './home/home.component';
import { ClientModule } from './client/client.module';

@NgModule({
	declarations: [
		AppComponent,
		AboutComponent,
		NavigationComponent,
		DashboardComponent,
		HomeComponent
	],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		HttpClientModule,
		HewiNgLibModule,
		MenubarModule,
		ButtonModule,
		MessagesModule,
		AppRoutingModule,
		AuthModule.forRoot(),
		ClientModule.forRoot(),
		StoreModule.forRoot(reducers, {
			metaReducers,
			runtimeChecks: {
				strictStateImmutability: true,
				strictActionImmutability: true
			}
		}),
		StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: environment.production }),
		EffectsModule.forRoot([])
	],
	providers: [
		GlobalErrorHandlerService, { provide: ErrorHandler, useClass: GlobalErrorHandlerService },
		MessageService
	],
	bootstrap: [AppComponent]
})
export class AppModule { }
