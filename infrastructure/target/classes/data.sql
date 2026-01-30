INSERT INTO clientes (dni, nombre, apellido1, apellido2, fecha_nacimiento) VALUES
    ('11111111A','Juan','Pérez','López','1959-09-12'),
    ('22222222B','Raúl','Canales','Rodríguez','1985-03-01'),
    ('33333333C','Elena','Ruiz','Herrera','2010-05-10'),
    ('44444444D','Raquel','Ruiz','Herrera','2002-06-21'),
    ('55555555E','María','Sánchez','Torres','1999-08-08');

INSERT INTO cuentas_bancarias (dni_cliente, tipo_cuenta, total) VALUES
    ('11111111A','PREMIUM',150000),
    ('11111111A','NORMAL',20000),
    ('22222222B','NORMAL',50000),
    ('22222222B','JUNIOR',300),
    ('33333333C','JUNIOR',300),
    ('44444444D','NORMAL',75000),
    ('55555555E','PREMIUM',120000);