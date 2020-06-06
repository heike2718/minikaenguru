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

export function getSucheDescription(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): string {

	let result = '';

	if (selectedKatalogItem) {
		switch (selectedKatalogItem.typ) {
			case 'LAND':
				result = 'Bitte suchen Sie Ihren Ort (mindestens 3 Buchstaben).';
				break;
			case 'ORT':
				result = 'Bitte suchen Sie Ihre Schule (mindestens 3 Buchstaben).';
				break
			case 'SCHULE':
				return '';
		}
		return result;
	}

	switch (alterKatalogtyp) {
		case 'LAND':
			result = 'Bitte geben Sie die Anfangsbuchstaben Ihres Landes ein (mindestens 3 Buchstaben).';
			break;
		case 'ORT':
			result = 'Bitte geben Sie die Anfangsbuchstaben Ihres Ortes ein (mindestens 3 Buchstaben).';
			break;
		case 'SCHULE':
			result = 'Bitte geben Sie die Anfangsbuchstaben Ihrer Schule ein';
			break;
	}

	return result;
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
		return '0 Treffer';
	}

	const katalogtyp = katalogItems[0].typ;
	let result = katalogItems.length + ' Treffer. ';

	switch (katalogtyp) {
		case 'LAND':
			result+= 'Bitte wählen Sie Ihr Land aus.';
			break;
		case 'ORT':
			result+= 'Bitte wählen Sie Ihren Ort aus.';
			break;
		case 'SCHULE':
			result+= 'Bitte wählen Sie Ihre Schule aus.';
			break;
	}

	return result;
}

export function getCurrentKatalogtyp(alterKatalogtyp: Katalogtyp, selectedKatalogItem: KatalogItem): Katalogtyp {

	if (selectedKatalogItem === undefined) {
		return alterKatalogtyp;
	}
	return selectedKatalogItem.typ;
}



