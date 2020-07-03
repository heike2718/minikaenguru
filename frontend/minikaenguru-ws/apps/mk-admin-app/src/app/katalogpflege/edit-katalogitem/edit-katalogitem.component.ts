import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'mka-edit-katalogitem',
  templateUrl: './edit-katalogitem.component.html',
  styleUrls: ['./edit-katalogitem.component.css']
})
export class EditKatalogitemComponent implements OnInit {

  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  gotoEditLand(): void {

	this.router.navigateByUrl('land/neu');

  }

  gotoEditOrt(): void {

	this.router.navigateByUrl('ort/neu');

  }

  gotoEditSchule(): void {

	this.router.navigateByUrl('schule/neu');

  }


}
