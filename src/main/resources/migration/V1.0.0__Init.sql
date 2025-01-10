CREATE TABLE Address (
    id SERIAL PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    country_code CHAR(2) NOT NULL CHECK (country_code ~ '^[A-Z]{2}$')
);

CREATE TABLE Users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    initial_login_pending BOOLEAN DEFAULT TRUE,
    address_id INT REFERENCES Address(id),
    role VARCHAR(255) NOT NULL
);
