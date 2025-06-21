create or replace view VW_ORTE as select DISTINCT(ORT_KUERZEL) AS KUERZEL, ORT_NAME AS NAME, LAND_KUERZEL, LAND_NAME, count(*) AS ANZAHL_SCHULEN from SCHULEN where ORT_NAME <> 'Unbekannt' group by ORT_KUERZEL;

