export type Katalogpflegetyp = 'LAND' | 'ORT' | 'SCHULE';


export interface KatalogpflegeItem {
	readonly typ: Katalogpflegetyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly pfad: string;
	readonly parent?: KatalogpflegeItem;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
	readonly kinderGeladen?: boolean;
};


export interface KatalogpflegeItemWithID {
	readonly kuerzel: string;
	readonly katalogItem: KatalogpflegeItem;
};

export interface Kataloge {
	readonly laender: KatalogpflegeItemWithID[];
	readonly orte: KatalogpflegeItemWithID[];
	readonly schulen: KatalogpflegeItemWithID[];
};

export interface SchulePayload {
	readonly name: string;
	readonly kuerzel: string;
	readonly kuerzelOrt: string;
	readonly nameOrt: string;
	readonly kuerzelLand: string;
	readonly nameLand: string;
};

export interface KuerzelAPIModel {
	readonly kuerzelSchule: string;
	readonly kuerzelOrt: string;
};

export function katalogpflegeItemWithIDToArray(itemsWithID: KatalogpflegeItemWithID[]): KatalogpflegeItem[] {

	const result: KatalogpflegeItem[] = [];
	itemsWithID.forEach(item => result.push(item.katalogItem));
	return result;

};

export function childrenAsArray(parent: KatalogpflegeItem, kataloge: Kataloge): KatalogpflegeItem[] {

	const result: KatalogpflegeItem[] = [];

	let katalog: KatalogpflegeItemWithID[];
	switch(parent.typ) {
		case 'LAND': katalog = kataloge.orte; break;
		case 'ORT': katalog = kataloge.schulen; break;
		case 'SCHULE': return result;
	}

	katalog.forEach(
		itemWithID => {
			if (itemWithID.katalogItem.parent && itemWithID.katalogItem.parent.kuerzel === parent.kuerzel) {
				result.push(itemWithID.katalogItem);
			}
		}
	)

	return result;
}

export function mergeKatalogItems(katalogItems: KatalogpflegeItem[], kataloge: Kataloge): Kataloge {

	let laender: KatalogpflegeItemWithID[] = [...kataloge.laender];
	let orte: KatalogpflegeItemWithID[] = [...kataloge.orte];
	let schulen: KatalogpflegeItemWithID[] = [...kataloge.schulen];

	katalogItems.forEach(item => {
		const typ = item.typ;

		switch (typ) {

			case 'LAND': laender = mergeKatalogItemMap(laender, item); break;
			case 'ORT': orte = mergeKatalogItemMap(orte, item); break;
			case 'SCHULE': schulen = mergeKatalogItemMap(schulen, item); break;
		}
	});

	return {
		laender: laender,
		orte: orte,
		schulen: schulen
	};
}

export function mergeKatalogItemMap(itemsWithID: KatalogpflegeItemWithID[], katalogItem: KatalogpflegeItem): KatalogpflegeItemWithID[] {

	let result: KatalogpflegeItemWithID[];

	if (!containsItem(itemsWithID, katalogItem)) {
		result = [ ...itemsWithID ];
		result.push({ kuerzel: katalogItem.kuerzel, katalogItem: katalogItem });
	} else {
		result = [];
		for (let i: number = 0; i < itemsWithID.length; i++) {
			const itemMitID: KatalogpflegeItemWithID = itemsWithID[i];
			if (itemMitID.kuerzel !== katalogItem.kuerzel) {
				result.push(itemMitID);
			} else {
				result.push({ kuerzel: katalogItem.kuerzel, katalogItem: katalogItem });
			}
		}
	}

	return result;
}


// ================= private functions =======================================//

function containsItem(itemsWithID: KatalogpflegeItemWithID[], katalogItem: KatalogpflegeItem): boolean {

	if (!itemsWithID || itemsWithID.length === 0) {
		return false;
	}

	for (let ind: number = 0; ind < itemsWithID.length; ind++) {

		if (itemsWithID[ind] && itemsWithID[ind].kuerzel === katalogItem.kuerzel) {
			return true;
		}
	}

	return false;
}


