import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatTabsModule } from '@angular/material/tabs';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';

import { AppComponent } from './app.component';
import { AboutComponent } from './about/about.component';
import { AuthModule } from './auth/auth.module';
import { NavigationComponent } from './navigation/navigation.component';
import { LayoutModule } from '@angular/cdk/layout';
import { DashboardComponent } from './dashboard/dashboard.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { AppRoutingModule } from './app-routing.module';

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
		MatMenuModule,
		MatButtonModule,
		MatIconModule,
		MatCardModule,
		MatTabsModule,
		MatSidenavModule,
		MatListModule,
		MatToolbarModule,
		AppRoutingModule,
		AuthModule.forRoot(),
		LayoutModule,
		MatGridListModule
	],
	providers: [],
	bootstrap: [AppComponent]
})
export class AppModule { }
