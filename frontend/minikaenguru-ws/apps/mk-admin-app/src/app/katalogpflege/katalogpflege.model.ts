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

export interface LandPayload {
	readonly name: string;
	readonly kuerzel: string;
};

export interface OrtPayload {
	readonly name: string;
	readonly kuerzel: string;
	readonly kuerzelLand: string;
	readonly nameLand: string;
};

export interface SchulePayload {
	readonly name: string;
	readonly kuerzel: string;
	readonly kuerzelOrt: string;
	readonly nameOrt: string;
	readonly kuerzelLand: string;
	readonly nameLand: string;
	readonly emailAuftraggeber?: string;
};

export interface KuerzelAPIModel {
	readonly kuerzelSchule: string;
	readonly kuerzelOrt: string;
};

export class KatalogPflegeItemsMap {

	private itemsMap: Map<string, KatalogpflegeItem> = new Map();

	constructor(private items: KatalogpflegeItemWithID[]) {

		if (items !== undefined) {
			for (const item of items) {
				this.itemsMap.set(item.kuerzel, item.katalogItem);
			}
		}
	}

	public toArray(): KatalogpflegeItem[] {
		return [...this.itemsMap.values()];
	}

	public has(kuerzel: string): boolean {

		return this.itemsMap.has(kuerzel);
	}

	public merge(katalogItem: KatalogpflegeItem): KatalogpflegeItemWithID[] {

		if (!this.has(katalogItem.kuerzel)) {
			const result: KatalogpflegeItemWithID[] = this.items !== undefined ? [...this.items] : [];
			result.push({ kuerzel: katalogItem.kuerzel, katalogItem: katalogItem });
			return result;
		} else {
			const result = [];
			for (const itemMitID of this.items) {
				if (itemMitID.kuerzel !== katalogItem.kuerzel) {
					result.push(itemMitID);
				} else {
					result.push({ kuerzel: katalogItem.kuerzel, katalogItem: katalogItem });
				}
			}
			return result;
		}
	}
}

export function childrenAsArray(parent: KatalogpflegeItem, kataloge: Kataloge): KatalogpflegeItem[] {

	const result: KatalogpflegeItem[] = [];

	let katalog: KatalogpflegeItemWithID[] = [];
	switch (parent.typ) {
		case 'LAND': katalog = kataloge.orte; break;
		case 'ORT': katalog = kataloge.schulen; break;
		case 'SCHULE': break;
	}

	if (katalog !== undefined) {
		katalog.forEach(
			itemWithID => {
				if (itemWithID.katalogItem.parent && itemWithID.katalogItem.parent.kuerzel === parent.kuerzel) {
					result.push(itemWithID.katalogItem);
				}
			}
		);
	}

	return result;
}

export function mergeKatalogItems(katalogItems: KatalogpflegeItem[], kataloge: Kataloge): Kataloge {

	const laenderMap: KatalogPflegeItemsMap = new KatalogPflegeItemsMap(kataloge.laender);
	const orteMap: KatalogPflegeItemsMap = new KatalogPflegeItemsMap(kataloge.orte);
	const schulenMap: KatalogPflegeItemsMap = new KatalogPflegeItemsMap(kataloge.schulen);

	let laender: KatalogpflegeItemWithID[];
	let orte: KatalogpflegeItemWithID[];
	let schulen: KatalogpflegeItemWithID[];

	katalogItems.forEach(item => {
		const typ = item.typ;

		switch (typ) {

			case 'LAND': laender = laenderMap.merge(item); break;
			case 'ORT': orte = orteMap.merge(item); break;
			case 'SCHULE': schulen = schulenMap.merge(item); break;
		}
	});

	return {
		laender: laender,
		orte: orte,
		schulen: schulen
	};
}

