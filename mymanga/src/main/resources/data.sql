INSERT INTO role (role_id, name) VALUES (1, 'ADMIN') ON CONFLICT (role_id) DO NOTHING;
INSERT INTO role (role_id, name) VALUES (2, 'BASIC') ON CONFLICT (role_id) DO NOTHING;
INSERT INTO role (role_id, name) VALUES (3, 'SUBSCRIBER') ON CONFLICT (role_id) DO NOTHING;

INSERT INTO manga (title, author, description, rating, keywords, status, genres) VALUES
    ('Gachiakuta', 'Kei Urana', 'Um garoto que vive em uma favela de descendentes de criminosos luta para sobreviver e provar sua inocência.', 8.2, 'Ação, Fantasia Sombria, Sobrenatural', 'RELEASING', 'FANTASY'),
    ('Dandadan', 'Yukinobu Tatsu', 'Uma garota que acredita em fantasmas e um garoto que acredita em alienígenas se unem para lutar contra forças paranormais.', 8.5, 'Comédia, Ação, Sci-Fi, Ocultismo', 'RELEASING', 'SCI_FI'),
    ('One Piece', 'Eiichiro Oda', 'As aventuras de Monkey D. Luffy e sua tripulação de piratas em busca do maior tesouro do mundo, o One Piece.', 9.2, 'Aventura, Fantasia, Ação', 'RELEASING', 'ADVENTURE'),
    ('The Apothecary Diaries', 'Natsu Hyuuga', 'Uma jovem apotecária é levada para o palácio imperial e resolve mistérios médicos e criminais na corte.', 8.8, 'Mistério, Histórico, Romance', 'RELEASING', 'SUSPENSE'),
    ('Fullmetal Alchemist', 'Hiromu Arakawa', 'Dois irmãos alquimistas procuram a Pedra Filosofal para restaurar seus corpos após uma tentativa falha de transmutação humana.', 9.1, 'Aventura, Fantasia Sombria, Steampunk', 'COMPLETED', 'FANTASY'),
    ('One Punch Man', 'ONE & Yusuke Murata', 'Saitama, um herói tão poderoso que consegue derrotar qualquer inimigo com um único soco, enfrenta a crise existencial de ser forte demais.', 8.9, 'Ação, Comédia, Super-herói', 'RELEASING', 'ACTION'),
    ('Berserk', 'Kentaro Miura', 'Guts, um ex-mercenário, busca vingança contra um antigo amigo que se tornou um demônio.', 9.4, 'Fantasia Sombria, Ação, Horror', 'HIATUS', 'FANTASY'),
    ('Vagabond', 'Takehiko Inoue', 'A história fictícia da jornada do lendário espadachim Miyamoto Musashi em busca de iluminação através do caminho da espada.', 9.0, 'Histórico, Artes Marciais, Samurai', 'HIATUS', 'DRAMA'),
    ('Attack on Titan', 'Hajime Isayama', 'A humanidade luta para sobreviver dentro de cidades muradas contra titãs gigantes que devoram humanos.', 8.6, 'Ação, Fantasia Sombria, Pós-apocalíptico', 'COMPLETED', 'ACTION'),
    ('Spy x Family', 'Tatsuya Endo', 'Um espião precisa construir uma família de mentira para uma missão, sem saber que a esposa é uma assassina e a filha uma telepata.', 8.7, 'Comédia, Ação, Slice of Life', 'RELEASING', 'COMEDY');

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 32.90, '1-4', '2022-05-12', 100, 1),
    (2, 32.90, '5-8', '2022-08-10', 95, 1),
    (3, 32.90, '9-12', '2022-11-15', 90, 1);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 33.90, '1-6', '2021-08-04', 120, 2),
    (2, 33.90, '7-14', '2021-10-04', 110, 2),
    (3, 33.90, '15-22', '2022-01-04', 105, 2);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 28.90, '1-8', '1997-12-24', 200, 3),
    (2, 28.90, '9-17', '1998-04-03', 198, 3),
    (3, 28.90, '18-26', '1998-06-04', 195, 3);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 36.90, '1-5', '2017-09-25', 80, 4),
    (2, 36.90, '6-10', '2018-02-24', 75, 4),
    (3, 36.90, '11-15', '2018-07-25', 72, 4);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 42.90, '1-4', '2002-01-22', 150, 5),
    (2, 42.90, '5-8', '2002-05-22', 148, 5),
    (3, 42.90, '9-12', '2002-09-21', 145, 5);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 29.90, '1-8', '2012-12-04', 180, 6),
    (2, 29.90, '9-16', '2012-12-05', 177, 6),
    (3, 29.90, '17-20', '2013-04-05', 170, 6);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 49.90, '0A-0H', '1990-11-26', 130, 7),
    (2, 49.90, '0I-0P', '1991-03-01', 125, 7),
    (3, 49.90, '1-6', '1991-10-03', 120, 7);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 45.90, '1-10', '1999-03-23', 90, 8),
    (2, 45.90, '11-20', '1999-07-23', 88, 8),
    (3, 45.90, '21-31', '1999-10-22', 85, 8);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 27.90, '1-4', '2010-03-17', 160, 9),
    (2, 27.90, '5-8', '2010-07-16', 155, 9),
    (3, 27.90, '9-12', '2010-12-09', 152, 9);

INSERT INTO manga_volume (volume_number, price, chapters, release_date, quantity, manga_id) VALUES
    (1, 31.90, '1-5', '2019-07-04', 190, 10),
    (2, 31.90, '6-11', '2019-10-04', 185, 10),
    (3, 31.90, '12-17', '2020-01-04', 180, 10);