CREATE SEQUENCE weight_goal_seq START WITH 1 INCREMENT BY 1 NOCACHE NOCYCLE;

CREATE TABLE weight_goal (
    id NUMBER(19) PRIMARY KEY,
    user_id NUMBER(19) NOT NULL,
    start_weight_kg NUMBER(5,2) NOT NULL,
    target_weight_kg NUMBER(5,2) NOT NULL,
    target_date DATE,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_weight_goal_user FOREIGN KEY (user_id) REFERENCES app_user(id),
    CONSTRAINT uq_weight_goal_user UNIQUE (user_id),
    CONSTRAINT ck_weight_goal_start_positive CHECK (start_weight_kg > 0),
    CONSTRAINT ck_weight_goal_target_positive CHECK (target_weight_kg > 0)
);
