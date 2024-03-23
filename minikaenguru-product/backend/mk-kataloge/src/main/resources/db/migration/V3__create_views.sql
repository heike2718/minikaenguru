create or replace view VW_ORTE as select DISTINCT(ORT_KUERZEL) AS KUERZEL, ORT_NAME AS NAME, LAND_KUERZEL, LAND_NAME, count(*) AS ANZAHL_SCHULEN from SCHULEN group by ORT_KUERZEL;

create or replace view VW_LAENDER as select DISTINCT(LAND_KUERZEL) AS KUERZEL, LAND_NAME AS NAME, count(*) AS ANZAHL_ORTE from VW_ORTE group by LAND_KUERZEL;
