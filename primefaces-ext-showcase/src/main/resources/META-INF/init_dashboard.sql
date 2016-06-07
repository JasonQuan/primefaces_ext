INSERT INTO base_dashboard_model (`ID`,`CUSTOMEKEY`)  VALUES ('1','default');

INSERT INTO base_panel_model (`ID`, `COLUMNINDEX`, `CUSTOMEKEY`, `HEADER`, `INCLUDE`, `ITEMINDEX`, `STYLE`, `VERSION`)
VALUES (concat(char(round((rand())*25)+97),char(round((rand())*25)+65),char(round((rand())*25)+65),char(round((rand())*25)+65),char(round((rand())*25)+65)), 
CEILING((RAND() * 6)) , 'default',
 concat(
char(round((rand())*25)+97),
char(round((rand())*25)+65),
char(round((rand())*25)+65),
char(round((rand())*25)+65),
char(round((rand())*25)+65)
), '/include.xhtml', CEILING((RAND() * 3)), 'auto;', 1);

