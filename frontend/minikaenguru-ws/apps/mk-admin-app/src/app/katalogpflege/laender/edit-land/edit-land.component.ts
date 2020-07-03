import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
	selector: 'mka-edit-land',
	templateUrl: './edit-land.component.html',
	styleUrls: ['./edit-land.component.css']
})
export class EditLandComponent implements OnInit {

	theId: string;

	constructor(private activatedRoute: ActivatedRoute) { }

	ngOnInit(): void {

		this.theId = this.activatedRoute.snapshot.paramMap.get('id');


	}

}
