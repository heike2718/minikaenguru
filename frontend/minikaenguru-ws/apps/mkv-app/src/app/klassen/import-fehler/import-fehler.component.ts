import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'mkv-import-fehler',
  templateUrl: './import-fehler.component.html',
  styleUrls: ['./import-fehler.component.css']
})
export class ImportFehlerComponent implements OnInit {

  @Input()
  fehler!: string

  constructor() { }

  ngOnInit() {
  }

}
