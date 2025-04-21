import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { LayoutComponent } from '@mks/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SidenavComponent } from './navigation/sidenav/sidenav.component';
import { HeaderComponent } from './navigation/header/header.component';
import { MessagesUiComponent, LoadingIndicatorComponent } from '@mks/messages-ui';
import { DomainFacade } from '@mks/domain-api';

@Component({
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
    selector: 'mks-root',
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  #domainFacade = inject(DomainFacade);

  ngOnInit(): void {
    this.#domainFacade.loadWettbewerbe();
  }
}
