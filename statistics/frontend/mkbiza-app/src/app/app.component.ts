import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LayoutComponent } from './domains/shared/layout/src/lib/layout/layout.component';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidenavComponent } from './domains/navigation/sidenav/sidenav.component';
import { HeaderComponent } from './domains/navigation/header/header.component';

@Component({
  standalone: true,
  imports: [
    MatToolbarModule,
    MatSidenavModule,
    LayoutComponent,
    SidenavComponent,
    HeaderComponent,
    RouterOutlet
  ],
  selector: 'mkbiza-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {

  opened = true;

  events: string[] = [];

}
