CREATE SEQUENCE app_user_seq START WITH 2 INCREMENT BY 1 NOCACHE NOCYCLE;
CREATE SEQUENCE weight_entry_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE app_user (
    id NUMBER(19) PRIMARY KEY,
    username VARCHAR2(100) NOT NULL,
    display_name VARCHAR2(150) NOT NULL,
    timezone VARCHAR2(60) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE weight_entry (
    id NUMBER(19) PRIMARY KEY,
    user_id NUMBER(19) NOT NULL,
    entry_date DATE NOT NULL,
    weight_kg NUMBER(5,2) NOT NULL,
    notes VARCHAR2(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_weight_entry_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT uq_weight_entry_user_date UNIQUE (user_id, entry_date),
    CONSTRAINT ck_weight_entry_positive CHECK (weight_kg > 0)
);

INSERT INTO app_user (id, username, display_name, timezone, created_at)
VALUES (1, 'demo', 'Usuario Demo', 'Europe/Madrid', CURRENT_TIMESTAMP);
