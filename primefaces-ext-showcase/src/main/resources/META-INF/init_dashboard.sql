delete from BASE_PANEL_MODEL;
delete from BASE_DASHBOARD_MODEL;
INSERT INTO BASE_DASHBOARD_MODEL (`ID`,`CUSTOMEKEY`)  VALUES ('1','default');
INSERT INTO BASE_PANEL_MODEL (`ID`, `COLUMNINDEX`, `CUSTOMEKEY`, `HEADER`, `INCLUDE`, `ITEMINDEX`, `STYLE`, `VERSION`)
VALUES (concat(char(round((rand())*25)+97),char(round((rand())*25)+65),char(round((rand())*25)+65),char(round((rand())*25)+65),char(round((rand())*25)+65)), 
CEILING((RAND() * 6)) , 'default',
 concat(
char(round((rand())*25)+97),
char(round((rand())*25)+65),
char(round((rand())*25)+65),
char(round((rand())*25)+65),
char(round((rand())*25)+65)
), '/include.xhtml', CEILING((RAND() * 3)), 'auto;', 1);

