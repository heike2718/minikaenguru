import { Component, OnInit } from '@angular/core';
import {MenuItem} from 'primeng/api';

@Component({
	selector: 'mkadm-navigation',
	templateUrl: './navigation.component.html',
	styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

	activeIndex = 0;
	items: MenuItem[];

	ngOnInit() {

		this.items = [
			{
				label: 'File',
				icon: 'fa-file-o',
				items: [{
					label: 'New',
					icon: 'fa-plus',
					items: [
						{ label: 'Project' },
						{ label: 'Other' },
					]
				},
				{ label: 'Open' },
				{ label: 'Quit' }
				]
			},
			{
				label: 'Edit',
				icon: 'fa-edit',
				items: [
					{ label: 'Undo', icon: 'fa-folder-open' },
					{ label: 'Redo', icon: 'fas fa-reply' }
				]
			},
			{
				label: 'Help',
				icon: 'fa-question',
				items: [
					{
						label: 'Contents'
					},
					{
						label: 'Search',
						icon: 'fa-search',
						items: [
							{
								label: 'Text',
								items: [
									{
										label: 'Workspace'
									}
								]
							},
							{
								label: 'File'
							}
						]
					}
				]
			},
			{
				label: 'Actions',
				icon: 'fa-gear',
				items: [
					{
						label: 'Edit',
						icon: 'fa-refresh',
						items: [
							{ label: 'Save', icon: 'fa-save' },
							{ label: 'Update', icon: 'fa-save' },
						]
					},
					{
						label: 'Other',
						icon: 'fa-phone',
						items: [
							{ label: 'Delete', icon: 'fa-minus' }
						]
					}
				]
			},
			{
				label: 'Quit', icon: 'fa-minus'
			}
		];

	}
}

