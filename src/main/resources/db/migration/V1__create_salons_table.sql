CREATE TABLE salons (
                        id BIGSERIAL PRIMARY KEY,

                        google_place_id VARCHAR(255) UNIQUE,
                        name VARCHAR(255) NOT NULL,
                        address VARCHAR(500) NOT NULL,
                        district VARCHAR(100) NOT NULL,

                        phone VARCHAR(100),
                        website_url VARCHAR(500),
                        social_media_url VARCHAR(500),
                        services TEXT,
                        price_range VARCHAR(100),

                        rating NUMERIC(2,1),
                        review_count INTEGER,

                        latitude DOUBLE PRECISION,
                        longitude DOUBLE PRECISION,
                        google_maps_url VARCHAR(500),
                        business_status VARCHAR(100)
);
