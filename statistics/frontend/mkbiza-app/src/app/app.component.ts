import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { LayoutComponent } from '@mkbiza-app/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidenavComponent } from './navigation/sidenav/sidenav.component';
import { HeaderComponent } from './navigation/header/header.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

@Component({
  standalone: true,
  imports: [
    BrowserAnimationsModule,
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
export class AppComponent implements OnInit {

  #router = inject(Router);

  ngOnInit(): void {
      this.#router.navigateByUrl('/');
  }

}
