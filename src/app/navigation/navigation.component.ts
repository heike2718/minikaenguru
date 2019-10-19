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
				icon: 'pi pi-file',
				items: [{
					label: 'New',
					icon: 'pi pi-plus',
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
				icon: 'pi pi-pencil',
				items: [
					{ label: 'Undo', icon: 'pi pi-folder-open' },
					{ label: 'Redo' }
				]
			},
			{
				label: 'Help',
				icon: 'pi pi-question',
				items: [
					{
						label: 'Contents'
					},
					{
						label: 'Search',
						icon: 'pi pi-search',
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
				icon: 'pi pi-cog',
				items: [
					{
						label: 'Edit',
						icon: 'pi pi-refresh',
						items: [
							{ label: 'Save', icon: 'pi pi-save' },
							{ label: 'Update', icon: 'pi pi-refresh' },
						]
					},
					{
						label: 'Other',
						items: [
							{ label: 'Delete', icon: 'pi pi-times' }
						]
					}
				]
			},
			{
				label: 'Quit', icon: 'pi pi-sign-out'
			}
		];

	}
}

