ALTER TABLE app_user ADD (
    email VARCHAR2(150),
    password_hash VARCHAR2(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    active NUMBER(1) DEFAULT 1 NOT NULL
);

ALTER TABLE app_user ADD CONSTRAINT uq_app_user_username UNIQUE (username);
ALTER TABLE app_user ADD CONSTRAINT uq_app_user_email UNIQUE (email);

UPDATE app_user
SET username = 'juanmaqueda',
    display_name = 'Juan Maqueda Vargas',
    email = 'juan@weight-tracker-calendar.local',
    password_hash = '$2a$10$TYavl/Ue35UW/9qmBBCGv.wb2oL36MmgC9DT1GPqVtTMqN.Md/eYC',
    updated_at = CURRENT_TIMESTAMP,
    active = 1
WHERE id = 1;

ALTER TABLE app_user MODIFY (password_hash NOT NULL);
