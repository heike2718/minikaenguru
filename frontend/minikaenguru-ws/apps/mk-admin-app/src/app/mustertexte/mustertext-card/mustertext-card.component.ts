import { Component, Input, OnInit } from '@angular/core';
import { Mustertext } from '../../shared/shared-entities.model';

@Component({
  selector: 'mka-mustertext-card',
  templateUrl: './mustertext-card.component.html',
  styleUrls: ['./mustertext-card.component.css']
})
export class MustertextCardComponent implements OnInit {

  @Input()
  mustertext!: Mustertext;

  constructor() { }

  ngOnInit(): void {
  }

}
