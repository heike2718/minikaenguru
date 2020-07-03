export type Katalogpflegetyp = 'LAND' | 'ORT' | 'SCHULE';


export interface KatalogpflegeItem {
	readonly typ: Katalogpflegetyp;
	readonly kuerzel: string;
	readonly name: string;
	readonly pfad: string;
	readonly parent: KatalogpflegeItem;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
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

export function katalogpflegeItemWithIDToArray(itemsWithID: KatalogpflegeItemWithID[]): KatalogpflegeItem[] {

	const result: KatalogpflegeItem[] = [];
	itemsWithID.forEach(item => result.push(item.katalogItem));
	return result;

};

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

// ================= private functions =======================================//

function containsItem(itemsWithID: KatalogpflegeItemWithID[], katalogItem: KatalogpflegeItem): boolean {

	const matching = itemsWithID.filter(itemMitID => { itemMitID.kuerzel === katalogItem.kuerzel });

	return matching.length > 0;
}

function mergeKatalogItemMap(itemsWithID: KatalogpflegeItemWithID[], katalogItem: KatalogpflegeItem): KatalogpflegeItemWithID[] {

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


