import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, ErrorHandler } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HewiNgLibModule } from 'hewi-ng-lib';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/components/button/button';
import { MessagesModule } from 'primeng/messages';
import { AppComponent } from './app.component';
import { AuthModule } from './auth/auth.module';
import { NavigationComponent } from './navigation/navigation.component';
import { StoreModule } from '@ngrx/store';
import { reducers, metaReducers } from './reducers';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { MessageService } from 'primeng/api';
import { GlobalErrorHandlerService } from './error/global-error-handler.service';
import { HomeComponent } from './home/home.component';
import { AuthInterceptor } from './shared/interceptors/auth.interceptor';
import { EffectsModule } from '@ngrx/effects';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
	{
		path: 'home',
		component: HomeComponent
	},
	{
		path: 'about',
		loadChildren: () => import('./about/about.module').then(m => m.AboutModule)
	},
	{
		path: 'files',
		loadChildren: () => import('./files/files.module').then(m => m.FilesModule)
	},
	{
		path: '**',
		redirectTo: '/home'
	}
];


@NgModule({
	declarations: [
		AppComponent,
		NavigationComponent,
		HomeComponent
	],
	imports: [
		BrowserModule,
		BrowserAnimationsModule,
		RouterModule.forRoot(routes, {useHash: true}),
		HttpClientModule,
		HewiNgLibModule,
		MenubarModule,
		ButtonModule,
		MessagesModule,
		AuthModule.forRoot(),
		StoreModule.forRoot(reducers, {
			metaReducers,
			runtimeChecks: {
				strictStateImmutability: true,
				strictActionImmutability: true
			}
		}),
		EffectsModule.forRoot([]),
		StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: environment.production })
	],
	providers: [
		GlobalErrorHandlerService, { provide: ErrorHandler, useClass: GlobalErrorHandlerService },
		{
			provide: HTTP_INTERCEPTORS,
			useClass: AuthInterceptor,
			multi: true
		},
		MessageService
	],
	bootstrap: [AppComponent]
})
export class AppModule { }
