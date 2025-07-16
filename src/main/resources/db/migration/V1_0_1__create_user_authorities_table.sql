ALTER TABLE authorities
    DROP CONSTRAINT fk_authorities_on_username;

CREATE TABLE user_authorities
(
    authority_id BIGINT      NOT NULL,
    username     VARCHAR(50) NOT NULL,
    CONSTRAINT pk_user_authorities PRIMARY KEY (authority_id, username)
);

ALTER TABLE authorities
    ADD created_at TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE authorities
    ADD description VARCHAR(255);

ALTER TABLE authorities
    ADD name VARCHAR(50);

ALTER TABLE authorities
    ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE authorities
    ALTER COLUMN name SET NOT NULL;

ALTER TABLE authorities
    ADD CONSTRAINT uc_authorities_name UNIQUE (name);

ALTER TABLE user_authorities
    ADD CONSTRAINT fk_useaut_on_authority FOREIGN KEY (authority_id) REFERENCES authorities (id);

ALTER TABLE user_authorities
    ADD CONSTRAINT fk_useaut_on_user FOREIGN KEY (username) REFERENCES users (username);

ALTER TABLE authorities
    DROP COLUMN authority;

ALTER TABLE authorities
    DROP COLUMN username;
