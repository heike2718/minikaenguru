export type Empfaengertyp = 'ALLE' | 'LEHRER' | 'PRIVATVERANSTALTER' | 'TEST';
export type StatusAuslieferung = 'WAITING' | 'IN_PROGRESS' | 'COMPLETED' | 'ERRORS';


export interface Newsletter {
	readonly uuid: string;
	readonly betreff: string;
	readonly text: string;
	readonly versandinfoIDs: string[];
};

export const initialNewsletter: Newsletter = {
	uuid: '',
	betreff: '',
	text: '',
	versandinfoIDs: []
};

export interface NewsletterWithID {
	readonly uuid: string;
	readonly newsletter: Newsletter;
};

export const initialNewsletterEditorModel: Newsletter = {
	uuid: 'neu',
	betreff: 'Minikänguru: ',
	text: '',
	versandinfoIDs: []
};

export interface Versandauftrag {
	readonly uuid: string;
	readonly newsletterID: string;
    readonly newsletterBetreff: string;
	readonly empfaengertyp: Empfaengertyp;
	readonly status: StatusAuslieferung;
	readonly anzahlAktuellVersendet: number;
	readonly anzahlEmpaenger: number;
	readonly versandBegonnenAm: string;
	readonly versandBeendetAm?: string;
	readonly versandMitFehler: boolean;
};

export interface VersandinfoWithID {
	readonly uuid: string;
	readonly versandinfo: Versandauftrag;
};

export interface NewsletterVersandauftrag {
	readonly newsletterID: string;
	readonly emfaengertyp: Empfaengertyp;
};

export class NewsletterMap {

	private newsletters: Map<string,Newsletter> = new Map();

	constructor(readonly items: NewsletterWithID[]) {

		if (items !== undefined) {
			for (const nl of items) {
				this.newsletters.set(nl.uuid, nl.newsletter);
			}
		}
	}

	public has(uuid: string): boolean {

		if (!uuid) {
			return false;
		}

		return this.newsletters.has(uuid);
	}

	public get(uuid: string): Newsletter | undefined {

		if (!this.has(uuid)) {
			return undefined;
		}

		return this.newsletters.get(uuid);
	}

	public toArray(): Newsletter[] {

		return [...this.newsletters.values()];
	}


	public merge(newsletter: Newsletter): NewsletterWithID[] {

		const result: NewsletterWithID[] = [];

		if (!this.has(newsletter.uuid)) {
			result.push({uuid: newsletter.uuid, newsletter: newsletter});
		}

		for(const item of this.items) {
			if (item.uuid !== newsletter.uuid) {
				result.push(item);
			} else {
				result.push({uuid: newsletter.uuid, newsletter: newsletter});
			}
		}

		return result;
	}

	public remove(newsletter: Newsletter): NewsletterWithID[] {

		const result: NewsletterWithID[] = [];

		for(const item of this.items) {
			if (item.uuid !== newsletter.uuid) {
				result.push(item);
			}
		}

		return result;
	}

};

export class VersandinfoMap {

	private versandinfos: Map<string,Versandauftrag> = new Map();

	constructor(readonly items: VersandinfoWithID[]) {

		if (items !== undefined) {
			for (const v of items) {
				this.versandinfos.set(v.uuid, v.versandinfo);
			}
		}
	}

	public has(uuid: string): boolean {

		if (!uuid) {
			return false;
		}

		return this.versandinfos.has(uuid);
	}

	public get(uuid: string): Versandauftrag | undefined {

		if (!this.has(uuid)) {
			return undefined;
		}

		return this.versandinfos.get(uuid);
	}

	public toArray(): Versandauftrag[] {

		return [...this.versandinfos.values()];
	}


	public merge(versandinfo: Versandauftrag): VersandinfoWithID[] {

		const result: VersandinfoWithID[] = [];

		for(const item of this.items) {
			if (item.uuid !== versandinfo.uuid) {
				result.push(item);
			} else {
				result.push({uuid: versandinfo.uuid, versandinfo: versandinfo});
			}
		}

		return result;
	}
};



