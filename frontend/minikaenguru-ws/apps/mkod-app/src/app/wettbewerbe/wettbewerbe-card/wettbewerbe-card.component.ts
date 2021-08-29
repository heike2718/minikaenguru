import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { TeilnahmenFacade } from '../../teilnahmen/teilnahmen.facade';
import { Wettbewerb } from '../wettbewerb.model';
import { WettbewerbeFacade } from '../wettbewerbe.facade';

@Component({
  selector: 'mkod-wettbewerbe-card',
  templateUrl: './wettbewerbe-card.component.html',
  styleUrls: ['./wettbewerbe-card.component.css']
})
export class WettbewerbeCardComponent implements OnInit {

  @Input()
  wettbewerb: Wettbewerb;

  wettbewerbUndefined: boolean;

  showBtnAnmeldungen = false;

  constructor(private router: Router,
    private wettbewerbeFacace: WettbewerbeFacade,
    private teilnahmenFacade: TeilnahmenFacade) { }

  ngOnInit(): void {

    this.wettbewerbUndefined = !this.wettbewerb;
    if (this.wettbewerb && this.wettbewerb.status !== 'BEENDET') {
      this.showBtnAnmeldungen = true;
    }
  }

  gotoAnmeldungen(): void {

    this.wettbewerbeFacace.selectWettbewerb(this.wettbewerb);
    this.router.navigateByUrl('/anmeldungen');

  }

  gotoTeilnahmen(): void {

    if (this.wettbewerb) {
      this.wettbewerbeFacace.selectWettbewerb(this.wettbewerb);
      this.router.navigateByUrl('/teilnahmen/' + this.wettbewerb.jahr);      
    }
  }

  gotoAufgaben(): void {
    this.wettbewerbeFacace.selectWettbewerb(this.wettbewerb);
  }

}
