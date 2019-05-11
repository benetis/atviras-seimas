SELECT count(*), faction_acronym, person_id
FROM vote
where
-- 	vote.person_id = 79150
-- and
	faction_acronym is not null
GROUP BY faction_acronym, person_id
HAVING COUNT(*) > 1;

----

SELECT count(*), faction_acronym, person_name, person_surname
FROM vote
where
-- 	vote.person_id = 79150
-- and
	faction_acronym is not null
and
	`time` >= '2016-11-14'
GROUP BY faction_acronym, person_name, person_surname
HAVING COUNT(*) > 1;


----
-- Working one

SELECT count(DISTINCT(faction_acronym)) as xParties, person_name, person_surname
from vote
where
-- 	vote.person_id = 79150
-- and
	faction_acronym is not null
and
	`time` >= '2016-11-14'
group by person_id, person_name, person_surname
HAVING xParties > 1;


--- Inserts results

insert into multi_factions_list (distinct_factions, person_name, person_surname, person_id, term_of_office_id)
SELECT count(DISTINCT(vote.faction_acronym)) as xParties, person_name, person_surname, person_id, 8
from vote
where
-- 	vote.person_id = 79150
-- and
	faction_acronym is not null
and
	`time` >= '2016-11-14'
group by person_id, person_name, person_surname
HAVING xParties > 1;