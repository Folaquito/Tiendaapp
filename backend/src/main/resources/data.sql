INSERT INTO productos (id, rawg_game_id, nombre, descripcion, imagen, precio, valoracion, en_stock) VALUES
    (1, 41494, 'Cyberpunk 2077', 'RPG futurista ambientado en Night City', 'https://media.rawg.io/media/games/26d/26d4437715bee60138dab4a7c8c59c92.jpg', 29990, 4.5, 5),
    (2, 326243, 'Elden Ring', 'Acción y fantasía en mundo abierto', 'https://media.rawg.io/media/games/b29/b294fdd866dcdb643e7bab370a552855.jpg', 34990, 4.8, 5),
    (3, 22509, 'Minecraft', 'Exploración y construcción con bloques', 'https://media.rawg.io/media/games/b4e/b4e4c73d5aa4ec66bbf75375c4847a2b.jpg', 12990, 4.7, 5);

-- Deja el autoincrement del ID después de los seeds para evitar colisiones al importar
ALTER TABLE productos ALTER COLUMN id RESTART WITH 1000;

-- Keys de ejemplo para demo (5 por producto)
INSERT INTO product_keys (code, status, producto_id) VALUES
    ('CYBE-ABCD-1234-EFGH', 'AVAILABLE', 1),
    ('CYBE-IJKL-5678-MNOP', 'AVAILABLE', 1),
    ('CYBE-QRST-9012-UVWX', 'AVAILABLE', 1),
    ('CYBE-YZ12-3456-ABCD', 'AVAILABLE', 1),
    ('CYBE-EF78-9012-GHIJ', 'AVAILABLE', 1),
    ('ELDE-AAAA-1111-BBBB', 'AVAILABLE', 2),
    ('ELDE-CCCC-2222-DDDD', 'AVAILABLE', 2),
    ('ELDE-EEEE-3333-FFFF', 'AVAILABLE', 2),
    ('ELDE-GGGG-4444-HHHH', 'AVAILABLE', 2),
    ('ELDE-IIII-5555-JJJJ', 'AVAILABLE', 2),
    ('MINE-1111-AAAA-2222', 'AVAILABLE', 3),
    ('MINE-3333-BBBB-4444', 'AVAILABLE', 3),
    ('MINE-5555-CCCC-6666', 'AVAILABLE', 3),
    ('MINE-7777-DDDD-8888', 'AVAILABLE', 3),
    ('MINE-9999-EEEE-0000', 'AVAILABLE', 3);
