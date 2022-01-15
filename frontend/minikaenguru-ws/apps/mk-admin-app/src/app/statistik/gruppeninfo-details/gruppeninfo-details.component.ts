import { Component, Input, OnInit } from '@angular/core';
import { StatistikGruppenitem } from '../statistik.model';

@Component({
  selector: 'mka-gruppeninfo-details',
  templateUrl: './gruppeninfo-details.component.html',
  styleUrls: ['./gruppeninfo-details.component.css']
})
export class GruppeninfoDetailsComponent implements OnInit {


  @Input()
  gruppenItem!: StatistikGruppenitem;

  constructor() { }

  ngOnInit(): void {
  }

}
