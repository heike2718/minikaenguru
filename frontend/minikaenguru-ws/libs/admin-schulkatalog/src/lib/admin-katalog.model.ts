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

export interface LandPayload {
	readonly name: string;
	readonly kuerzel: string;
};

export const initialLandPayload: LandPayload = {
	name: '',
	kuerzel: ''
};

export interface OrtPayload {
	readonly name: string;
	readonly kuerzel: string;
	readonly kuerzelLand: string;
	readonly nameLand: string;
};

export const initialOrtPayload: OrtPayload = {
	name: '',
	kuerzel: '',
	kuerzelLand: '',
	nameLand: ''
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

export const initialSchulePayload: SchulePayload = {
	name: '',
	kuerzel: '',
	kuerzelOrt: '',
	nameOrt: '',
	kuerzelLand: '',
	nameLand: ''
}

export interface KuerzelResponseDto {
	readonly kuerzelSchule: string;
	readonly kuerzelOrt: string;
};

export interface SchuleEditorModel {
	readonly modusCreate: boolean;
	readonly emailAuftraggeber: string;	
	readonly schulePayload: SchulePayload;
	readonly kuerzelLandDisabled: boolean;
	readonly nameLandDisabled: boolean;
	readonly nameOrtDisabled: boolean;
};

export const initialSchuleEditorModel: SchuleEditorModel = {
	modusCreate: true,
	emailAuftraggeber: '',
	schulePayload: initialSchulePayload,
	kuerzelLandDisabled: false, // wenn das Land mit angelegt werden muss
	nameLandDisabled: false, // wenn das Land mit angelegt werden muss
	nameOrtDisabled: false // wenn der Ort mit angelegt werden muss
};

export interface SchuleEditorModelAndKuerzel {
	schuleEditorModel: SchuleEditorModel;
	kuerzel: KuerzelResponseDto;
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
