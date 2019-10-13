import { NgModule, ModuleWithProviders } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';



@NgModule({
	declarations: [],
	imports: [
		CommonModule,
		ReactiveFormsModule,
		MatCardModule,
		MatInputModule,
		MatButtonModule,
	]
})
export class AuthModule {
	static forRoot(): ModuleWithProviders {
		return {
			ngModule: AuthModule,
			providers: []
		};
	}
}
