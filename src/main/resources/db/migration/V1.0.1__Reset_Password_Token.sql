CREATE TABLE user_reset_password_tokens(
    id          UUID PRIMARY KEY NOT NULL,
    user_id     UUID             NOT NULL,
    reset_token VARCHAR(255)     NOT NULL UNIQUE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX user_reset_password_tokens_reset_token_key ON user_reset_password_tokens (reset_token);