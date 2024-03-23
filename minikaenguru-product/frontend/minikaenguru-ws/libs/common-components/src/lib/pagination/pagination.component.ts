import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { initialPaginationComponentModel, PaginationComponentModel } from '../common-components.model';

@Component({
	selector: 'mk-pagination',
	templateUrl: './pagination.component.html',
	styleUrls: ['./pagination.component.css']
})
export class PaginationComponent implements OnInit {

    page = 1; // Nummer des gewählten Links
  
    @Input()
    options: PaginationComponentModel = initialPaginationComponentModel;

    @Output()
    pageSelected: EventEmitter<number> = new EventEmitter();

    ngOnInit(): void {}

    onPageChanged($event: number): void {
        this.pageSelected.emit($event);
    }
}