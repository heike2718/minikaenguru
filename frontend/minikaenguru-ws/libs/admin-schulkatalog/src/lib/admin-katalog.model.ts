export interface Land {
    readonly kuerzel: string;
	readonly name: string;
    readonly pfad: string;
	readonly anzahlKinder: number;
};

export interface Ort {
    readonly land: Land;
    readonly kuerzel: string;
	readonly name: string;
    readonly pfad: string;
	readonly anzahlKinder: number;
};

export interface Schule {
    readonly land: Land;
    readonly ort: Ort;
    readonly kuerzel: string;
	readonly name: string;
    readonly pfad: string;
	readonly anzahlKinder: number;
};



export interface KatalogitemResponseDto {
    readonly kuerzel: string;
	readonly name: string;
	readonly pfad: string;
	readonly parent: KatalogitemResponseDto | undefined;
	readonly anzahlKinder: number;
	readonly leaf: boolean;
	readonly kinderGeladen?: boolean;
};

export interface OrtSucheResult {
    readonly land: Land;
    readonly orte: KatalogitemResponseDto[];
};

export interface SchuleSucheResult {
    readonly ort: Ort;
    readonly schulen: KatalogitemResponseDto[];
}

export function mapToLand(katalogItem: KatalogitemResponseDto): Land {

    return {
        name: katalogItem.name,
        pfad: katalogItem.pfad,
        kuerzel: katalogItem.kuerzel,
        anzahlKinder: katalogItem.anzahlKinder
    };
};

export function mapToOrtInLand(land: Land, katalogItem: KatalogitemResponseDto): Ort {

    return {
        land: land,
        name: katalogItem.name,
        pfad: katalogItem.pfad,
        kuerzel: katalogItem.kuerzel,
        anzahlKinder: katalogItem.anzahlKinder
    };
};

export function mapToSchuleInOrt(ort: Ort, katalogItem: KatalogitemResponseDto): Schule {

    return {
        land: ort.land,
        ort: ort,
        name: katalogItem.name,
        pfad: katalogItem.pfad,
        kuerzel: katalogItem.kuerzel,
        anzahlKinder: katalogItem.anzahlKinder
    };
};
