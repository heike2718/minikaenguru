export type StatistikEntity = 'KINDER' | 'LOESUNGSZETTEL';

export interface Auspraegung {
    readonly wert: string;
    readonly anzahl: number;
};

export interface StatistikGruppenitem {
    readonly name: string;
    readonly auspraegungen: Auspraegung[];
};

export interface StatistikGruppeninfo {
    readonly uuid: string;
	readonly anzahlElemente: number;
    readonly gruppenItems: StatistikGruppenitem[];
};

export interface StatistikGruppeninfoWithID {
    readonly uuid: string;
    readonly statistikGruppeninfo: StatistikGruppeninfo;
};

export class StatistikGruppeninfoMap {

    private gruppeninfos: Map<string, StatistikGruppeninfo> = new Map();

    constructor(readonly items: StatistikGruppeninfoWithID[]) {

        if (items != undefined) {
            for (const item of items) {
				this.gruppeninfos.set(item.uuid, item.statistikGruppeninfo);
			}
        }
    }

    public has(uuid: string): boolean {

		if (uuid === undefined) {
			return false;
		}
		return this.gruppeninfos.has(uuid);
	}

	get(uuid: string): StatistikGruppeninfo | undefined {

		if (uuid === undefined) {
			return undefined;
		}
		return this.gruppeninfos.get(uuid);
	}

    public toArray(): StatistikGruppeninfo[] {
		return [...this.gruppeninfos.values()];
	}

    public merge(gruppeninfo: StatistikGruppeninfo): StatistikGruppeninfoWithID[] {

		if (!this.has(gruppeninfo.uuid)) {
			const result: StatistikGruppeninfoWithID[] = this.items !== undefined ? [...this.items] : [];
			result.push({ uuid: gruppeninfo.uuid, statistikGruppeninfo: gruppeninfo });
			return result;
		} else {
			const result = [];
			for (const itemMitID of this.items) {
				if (itemMitID.uuid !== gruppeninfo.uuid) {
					result.push(itemMitID);
				} else {
					result.push({ uuid: gruppeninfo.uuid, statistikGruppeninfo: gruppeninfo  });
				}
			}
			return result;
		}
	}
}


