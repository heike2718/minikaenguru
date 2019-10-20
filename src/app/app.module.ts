import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { HewiNgLibModule } from 'hewi-ng-lib';


import {MenuModule} from 'primeng/components/menu/menu';
import {ButtonModule} from 'primeng/components/button/button';
import {GrowlModule} from 'primeng/components/growl/growl';


import { AppComponent } from './app.component';
import { AboutComponent } from './about/about.component';
import { AuthModule } from './auth/auth.module';
import { NavigationComponent } from './navigation/navigation.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AppRoutingModule } from './app-routing.module';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';

@NgModule({
	declarations: [
		AppComponent,
		AboutComponent,
		NavigationComponent,
		DashboardComponent
	],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		HttpClientModule,
		HewiNgLibModule,
		MenuModule,
		ButtonModule,
		GrowlModule,
		AppRoutingModule,
		AuthModule.forRoot(),
		StoreModule.forRoot(reducers, {
			metaReducers,
			runtimeChecks: {
				strictStateImmutability: true,
				strictActionImmutability: true
			}
		}),
		StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: environment.production })
	],
	providers: [],
	bootstrap: [AppComponent]
})
export class AppModule { }
