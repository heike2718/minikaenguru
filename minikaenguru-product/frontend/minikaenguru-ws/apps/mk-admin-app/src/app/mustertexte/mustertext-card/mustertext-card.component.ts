import { Component, Input, OnInit } from '@angular/core';
import { Mustertext } from '../../shared/shared-entities.model';
import { MustertexteFacade } from '../mustertexte.facade';

@Component({
  selector: 'mka-mustertext-card',
  templateUrl: './mustertext-card.component.html',
  styleUrls: ['./mustertext-card.component.css']
})
export class MustertextCardComponent implements OnInit {

  @Input()
  mustertext!: Mustertext;

  btnMailLabel = '';
  btnMailTooltip = '';

  constructor(private mustertexteFacade: MustertexteFacade) { }

  ngOnInit(): void {
   switch(this.mustertext.kategorie) {
     case 'MAIL': {this.btnMailLabel = 'Mail'; this.btnMailTooltip = 'Mail an Veranstalter erzeugen'; break}
     case 'NEWSLETTER': {this.btnMailLabel = 'Newsletter'; this.btnMailTooltip = 'Newsletter erzeugen'; break}
   }
  }

  aendern(): void {

    this.mustertexteFacade.startEditMustertext(this.mustertext);

  }

  mailErzeugen(): void {

    if (this.mustertext.kategorie === 'MAIL') {
      this.mustertexteFacade.createMail(this.mustertext);
    }

    if (this.mustertext.kategorie === 'NEWSLETTER') {
      this.mustertexteFacade.createNewsletter(this.mustertext);
    }

  }

  loeschen(): void {
    this.mustertexteFacade.deleteMustertext(this.mustertext);
  }

}
