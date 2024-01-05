drop table klienci;

begin;

create table klienci (
  idklienta   integer primary key,
  nazwa       varchar(130) not null,
  ulica       varchar(30) not null,
  miejscowosc varchar(15) not null,
  kod         char(6) not null,
  telefon     varchar(20) not null
);

copy klienci from stdin with (null '', delimiter '|');
1|Hłasko Regina|Edwarda Bera 5|Elbląg|91-001|111 222 111
2|Pikowski Stefan|Wolna 3|Kraków|92-111|012 111 11 11
3|Czarnkowska Dalia|Wolska 89|Iława|11-373|111 222 001
4|Wandziak Wojciech|Gregorowa 3|Warszawa|10-001|111 222 002
7|Wojak Alicja|Beringa 89|Wrocław|70-764|111 222 003
8|Górka Andrzej|Stefanowska 35|Gdańsk|11-788|111 222 004
10|Moniak Antoni|Młyńska 34|Kraków|91-001|012 222 22 00
11|Sokół Robert|Akacjowa 3|Kraków|92-111|012 111 11 00
12|Witak Nina|Kasztanowa 23|Warszawa|11-373|022 888 88 00
13|Walendziak Jarosław|Krucza 12|Warszawa|10-001|022 888 88 01
14|Piotrowska Regina|Rzeczna 44|Borki|74-013|123 456 002
15|Miszak Stefan|Boczna 23/91|Pomiechówek|70-764|123 456 003
16|Sowa Dalia|Krucza 12/43|Warszawa|11-788|123 456 004
18|Wolski Wojciech|Edwarda Bera 5|Lubinek|91-001|123 456 005
19|Wojak Mirosław|Akacjowa 3|Borki|92-111|777 001 001
21|Górka Alicja|Wolna12/89|Katowice|11-373|777 001 002
22|Moński Andrzej|Towarowa 3|Kraków|10-001|777 001 003
24|Sokół Antoni|Prosta 345|Siedlce|74-013|777 001 004
25|Heroński Robert|Barstefska 89|Gdańsk|70-764|777 001 005
26|Walendziak Tomasz|Rzeczna 4|Płock|91-001|777 001 006
27|Piotrowski Jaroslaw|Młyńska 34|Borki|92-111|777 001 007
28|Miszak Helena|Akacjowa 3|Kraków|11-373|012 555 00 11
29|Sowa Rafał|Kasztanowa 23|Siedlce|10-001|333 000 100
30|Wolska Regina|Krucza 12|Gdańsk|74-013|333 000 101
31|Fisiak Stefan|Rzeczna 44|Pomiechówek|70-764|333 000 102
33|Wojak Dalia|Boczna 23/91|Kraków|91-001|333 000 103
34|Górka Wojciech|Krucza 12/43|Siedlce|92-111|333 000 104
35|Moniak Mirosław|Trakt 10|Gdańsk|11-373|333 000 105
37|Sokół Alicja|Akacjowa 12/89|Borki|10-001|333 000 106
38|Helkowski Andrzej|Edwarda Bera 5|Katowice|74-013|333 000 107
39|Walendziak Antoni|Akacjowa 3|Kraków|70-764|012 111 11 55
40|Piotrowska Regina|Wolna12/89|Siedlce|11-788|787 345 012
42|Wojak Stefan|Towarowa 3|Gdańsk|79-408|787 345 013
43|Górka Dalia|Prosta 345|Dołowe|19-047|787 345 014
44|Moniak Wojciech|Beringa 89|Pomiechówek|91-001|787 345 015
46|Sokół Miroslaw|Rzeczna 4|Borki|92-111|787 345 016
47|Hern Alicja|Młyńska 34|Doły|11-373|787 345 017
48|Walendziak Andrzej|Akacjowa 3|Kraków|10-001|787 345 018
49|Piotrowski Antoni|Kasztanowa 23|Kownik|74-013|232 777 900
50|Miszak Robert|Krucza 12|Katowice|70-764|232 777 901
51|Sowa Tomasz|Edwarda Bera 5|Siedlce|11-788|232 777 902
52|Wolski Jarosław|Akacjowa 3|Sopot|91-001|232 777 903
53|Fisiak Helena|Wolna12/89|Pomiechówek|92-111|232 777 904
54|Fisiak Regina|Towarowa 3|Kraków|11-373|232 777 905
55|Wojak Stefan|Prosta 345|Siedlce|10-001|232 777 906
56|Górka Dalia|Beringa 89|Gdańsk|74-013|232 777 907
57|Moniak Wojciech|Rzeczna 4|Pomiechówek|70-764|232 777 908
58|Sokół Mirosław|Młyńska 34|Pomiechówek|11-788|232 777 909
59|Herron Alicja|Akacjowa 3|Wicie|79-408|564 345 303
62|Walendziak Andrzej|Kasztanowa 23|Siedlce|91-001|564 345 304
63|Piotrowski Antoni|Krucza 12|Kraków|92-111|012 334 44 56
64|Wojak Robert|Rzeczna 44|Siedlce|11-373|564 345 305
66|Górka Tomasz|Boczna 23/91|Gdańsk|10-001|564 345 306
67|Moniak Jarosław|Edwarda Bera 5|Michów|74-013|564 345 307
68|Witak Nina|Akacjowa 3|Siedlce|70-764|564 345 308
69|Herron Stefan|Wolna 12/89|Gdańsk|11-788|564 345 309
70|Walendziak Dalia|Towarowa 3|Tarnów|79-408|564 345 312
71|Piotrowski Wojciech|Prosta 345|Kraków|19-047|012 334 44 57
73|Miszak Mirosław|Beringa 89|Płock|81-230|746 006 020
74|Sowa Alicja|Rzeczna 4|Siedlce|91-001|746 006 021
76|Wolski Andrzej|Młyńska 34|Sokółka|92-111|746 006 022
79|Fisiak Antoni|Akacjowa 3|Konstancin|11-373|746 006 023
80|Wyga Robert|Kasztanowa 23|Płock|10-001|746 006 024
82|Fisiak Tomasz|Krucza 12|Katowice|74-013|746 006 025
84|Wojak Jarosław|Rzeczna 44|Ludwiki|70-764|746 006 026
85|Górka Helena|Boczna 23/91|Regina|11-788|746 006 027
86|Moniak Rafał|Krucza 12/43|Kraków|79-408|012 334 44 58
\.

commit;
