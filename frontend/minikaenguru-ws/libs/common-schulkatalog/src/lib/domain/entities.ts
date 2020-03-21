export interface KatalogItem {
	readonly typ: Katalogtyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly pfad: string;
	readonly parent: KatalogItem;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
}

export function compareKatalogItemsByName(ki1: KatalogItem, ki2: KatalogItem): number {
	return ki1.name.localeCompare(ki2.name);
}

export type Katalogtyp = 'LAND' | 'ORT' | 'SCHULE';


export interface GuiModel {
	readonly currentKatalogtyp: Katalogtyp, // 1
	readonly sucheDescription: string; // 5
	readonly inputLabel: string; // 6
	readonly auswahlDescription: string; // 7
	readonly showInputControl: boolean; // 8
	readonly showLoadingIndicator: boolean; // 9
	readonly katalogItemsAvailable: boolean; // 10
}

// loadedKatalogItems: KatalogItem[],  // 11
// 	searchTerm: string, // 12
// 	selectedKatalogItem: KatalogItem // 13


export function getSucheDescription(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): string {

	if (selectedKatalogItem) {
		switch (selectedKatalogItem.typ) {
			case 'LAND':
				return 'Bitte suchen Sie Ihren Ort.';
			case 'ORT':
				return 'Bitte suchen Sie Ihre Schule.';
			case 'SCHULE':
				return '';
		}
	}

	switch (alterKatalogtyp) {
		case 'LAND':
			return 'Bitte suchen Sie Ihr Land.';
		case 'ORT':
			return 'Bitte suchen Sie Ihren Ort.';
		case 'SCHULE':
			return 'Bitte suchen Sie Ihre Schule.';
	}

	return '';
}

export function getInputLabel(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): string {

	if (selectedKatalogItem) {
		switch (selectedKatalogItem.typ) {
			case 'LAND':
				return 'Ort';
			case 'ORT':
				return 'Schule';
			case 'SCHULE':
				return '';
		}
	}

	switch (alterKatalogtyp) {
		case 'LAND':
			return 'Land';
		case 'ORT':
			return 'Ort';
		case 'SCHULE':
			return 'Schule';
	}

	return '';
}

export function getAuswahlDescriptiom(katalogItems: KatalogItem[]): string {

	if (katalogItems.length === 0) {
		return '';
	}

	const katalogtyp = katalogItems[0].typ;

	switch (katalogtyp) {
		case 'LAND':
			return 'Bitte wählen Sie Ihr Land aus.';
		case 'ORT':
			return 'Bitte wählen Sie Ihren Ort aus.';
		case 'SCHULE':
			return 'Bitte wählen Sie Ihre Schule aus.';
	}

	return '';
}

export function getCurrentKatalogtyp(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): Katalogtyp {

	if (selectedKatalogItem === undefined) {
		return alterKatalogtyp;
	}
	return selectedKatalogItem.typ;
}



