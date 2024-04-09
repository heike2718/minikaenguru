import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LayoutComponent } from '@mkbiza-app/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidenavComponent } from './navigation/sidenav/sidenav.component';
import { HeaderComponent } from './navigation/header/header.component';
import { MessagesUiComponent, LoadingIndicatorComponent } from '@mkbiza-app/messages-ui';
import { DomainFacade } from '@mkbiza-app/domain-api';

@Component({
  standalone: true,
  imports: [
    MatToolbarModule,
    MatSidenavModule,
    LayoutComponent,
    SidenavComponent,
    HeaderComponent,
    RouterOutlet,
    MessagesUiComponent,
    LoadingIndicatorComponent
  ],
  selector: 'mkbiza-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit {

  #domainFacade = inject(DomainFacade);

  ngOnInit(): void {
    this.#domainFacade.loadWettbewerbe();
  }
}
