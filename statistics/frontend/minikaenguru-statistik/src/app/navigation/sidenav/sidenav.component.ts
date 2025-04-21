import { Component, EventEmitter, Output } from '@angular/core';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { RouterLinkWithHref } from '@angular/router';

@Component({
    selector: 'mks-sidenav',
    imports: [
        MatIconModule,
        MatListModule,
        MatToolbarModule,
        MatSidenavModule,
        RouterLinkWithHref
    ],
    templateUrl: './sidenav.component.html',
    styleUrl: './sidenav.component.scss'
})
export class SidenavComponent {

  version = '2.0.0';

  @Output()
  sidenavClose = new EventEmitter();

  public onSidenavClose = () => {
    // console.log('onSidenavClose');
    this.sidenavClose.emit();
  }

}
