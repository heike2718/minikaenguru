import { Component, Input, OnInit } from '@angular/core';
import { StatistikGruppeninfo } from '../statistik.model';

@Component({
  selector: 'mka-gruppeninfo-details',
  templateUrl: './gruppeninfo-details.component.html',
  styleUrls: ['./gruppeninfo-details.component.css']
})
export class GruppeninfoDetailsComponent implements OnInit {


  @Input()
  gruppeninfo?: StatistikGruppeninfo;

  constructor() { }

  ngOnInit(): void {}

  

}
