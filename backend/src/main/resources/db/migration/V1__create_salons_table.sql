CREATE TABLE salons (
                        id BIGSERIAL PRIMARY KEY,

                        name VARCHAR(255) NOT NULL,
                        address VARCHAR(255) NOT NULL,
                        district VARCHAR(100) NOT NULL,

                        phone_number VARCHAR(100),
                        website VARCHAR(500),
                        services TEXT,
                        price_range VARCHAR(100),
                        rating NUMERIC(2,1),
                        review_count INTEGER,

                        CONSTRAINT uk_salons_name_address UNIQUE (name, address)
);
