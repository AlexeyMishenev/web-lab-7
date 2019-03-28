CREATE TABLE Antibiotics
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    method     VARCHAR(255),
    skf_from   INTEGER      NOT NULL,
    skf_to     INTEGER      NOT NULL,
    dosage     VARCHAR(255) NOT NULL,
    additional VARCHAR(255)
);

INSERT INTO Antibiotics (name, method, skf_from, skf_to, dosage, additional)
VALUES ('Азитромицин', null, 0, 1000, '0.5 г каждые 24 часа*',
        'Назначение обычно  500 мг 1 раз в день в течении 3-х дней');
INSERT INTO Antibiotics (name, method, skf_from, skf_to, dosage, additional)
VALUES ('Азтреонам', null, 10, 50, '1 - 2 г каждые 12 - 24 часа',
        null);
INSERT INTO Antibiotics (name, method, skf_from, skf_to, dosage, additional)
VALUES ('Амикацин', 'в/в однократно в сутки', 30, 40, '4 мг/кг массы тела каждые 24 часа',
        null);
INSERT INTO Antibiotics (name, method, skf_from, skf_to, dosage, additional)
VALUES ('Ацикловир', 'per os', 50, 1000, '5 - 10 мг/кг каждые 8 часов*',
        'Возможно: по 0.2 г 5 раз в сутки');
INSERT INTO Antibiotics (name, method, skf_from, skf_to, dosage, additional)
VALUES ('Ципрофлоксацин', 'в/в капельно', 0, 10, '0.2 г каждые 24 часа*',
        'Инфузия в течение часа');
INSERT INTO Antibiotics (name, method, skf_from, skf_to, dosage, additional)
VALUES ('Эритромицин этилсукцинат', null, 0, 1000, '0.4 г каждые 6 часов',
        null);
INSERT INTO Antibiotics (name, method, skf_from, skf_to, dosage, additional)
VALUES ('Левофлоксацин', 'per os', 50, 1000, '250 - 500 мг каждые 24 часа',
        null);
