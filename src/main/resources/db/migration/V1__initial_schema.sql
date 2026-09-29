-- ============================================================
-- V1: Initial schema for social-commerce
-- Auto-run by Flyway on first boot; matches all JPA entities.
-- ============================================================

-- Users
CREATE TABLE IF NOT EXISTS users (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid          VARCHAR(36)  NOT NULL UNIQUE,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('BUYER','SELLER','ADMIN') NOT NULL,
    is_active     TINYINT(1)   NOT NULL DEFAULT 1,
    created_at    DATETIME
);

-- User profiles
CREATE TABLE IF NOT EXISTS user_profiles (
    user_id         BIGINT PRIMARY KEY,
    bio             TEXT,
    avatar_url      VARCHAR(500),
    website         VARCHAR(500),
    store_name      VARCHAR(255),
    followers_count INT DEFAULT 0,
    following_count INT DEFAULT 0,
    post_count      INT DEFAULT 0,
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Follows
CREATE TABLE IF NOT EXISTS follows (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    follower_id BIGINT NOT NULL,
    followee_id BIGINT NOT NULL,
    created_at  DATETIME,
    UNIQUE KEY uq_follow (follower_id, followee_id),
    CONSTRAINT fk_follow_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_follow_followee FOREIGN KEY (followee_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Categories
CREATE TABLE IF NOT EXISTS categories (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255),
    parent_id BIGINT,
    slug      VARCHAR(255),
    level     INT DEFAULT 0
);

-- Products
CREATE TABLE IF NOT EXISTS products (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid             VARCHAR(36)     NOT NULL UNIQUE,
    seller_id        BIGINT          NOT NULL,
    category_id      BIGINT,
    title            VARCHAR(255)    NOT NULL,
    description      TEXT,
    price            DECIMAL(12,2)   NOT NULL,
    status           ENUM('DRAFT','PENDING_REVIEW','ACTIVE','REJECTED','ARCHIVED') NOT NULL DEFAULT 'DRAFT',
    avg_rating       DOUBLE          DEFAULT 0.0,
    review_count     INT             DEFAULT 0,
    wishlist_count   INT             DEFAULT 0,
    rejection_reason VARCHAR(1000),
    created_at       DATETIME,
    updated_at       DATETIME,
    CONSTRAINT fk_product_seller   FOREIGN KEY (seller_id)   REFERENCES users(id),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Product images
CREATE TABLE IF NOT EXISTS product_images (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id    BIGINT       NOT NULL,
    image_url     VARCHAR(500) NOT NULL,
    display_order INT          DEFAULT 0,
    CONSTRAINT fk_product_images_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Product variants
CREATE TABLE IF NOT EXISTS product_variants (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id     BIGINT        NOT NULL,
    size           VARCHAR(50),
    color          VARCHAR(50),
    stock_quantity INT           NOT NULL DEFAULT 0,
    sku            VARCHAR(100)  UNIQUE,
    price_override DECIMAL(12,2),
    CONSTRAINT fk_variant_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Addresses
CREATE TABLE IF NOT EXISTS addresses (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       BIGINT       NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    phone         VARCHAR(20),
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city          VARCHAR(100) NOT NULL,
    state         VARCHAR(100) NOT NULL,
    pincode       VARCHAR(10)  NOT NULL,
    is_default    TINYINT(1)   DEFAULT 0,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Cart items
CREATE TABLE IF NOT EXISTS cart_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    added_at   DATETIME,
    UNIQUE KEY uq_cart_item (user_id, variant_id),
    CONSTRAINT fk_cart_user    FOREIGN KEY (user_id)    REFERENCES users(id)            ON DELETE CASCADE,
    CONSTRAINT fk_cart_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
);

-- Orders
CREATE TABLE IF NOT EXISTS orders (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid           VARCHAR(36)   NOT NULL UNIQUE,
    buyer_id       BIGINT        NOT NULL,
    address_id     BIGINT,
    total_amount   DECIMAL(12,2) NOT NULL,
    status         ENUM('PLACED','SHIPPED','DELIVERED','CANCELLED','RETURN_REQUESTED') NOT NULL DEFAULT 'PLACED',
    payment_status ENUM('PENDING','MOCK_PAID','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50),
    created_at     DATETIME,
    updated_at     DATETIME,
    CONSTRAINT fk_order_buyer   FOREIGN KEY (buyer_id)   REFERENCES users(id),
    CONSTRAINT fk_order_address FOREIGN KEY (address_id) REFERENCES addresses(id) ON DELETE SET NULL
);

-- Order items
CREATE TABLE IF NOT EXISTS order_items (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id          BIGINT       NOT NULL,
    product_id        BIGINT       NOT NULL,
    variant_id        BIGINT,
    product_title     VARCHAR(200) NOT NULL,
    variant_details   VARCHAR(200),
    quantity          INT          NOT NULL,
    price_at_purchase DECIMAL(12,2) NOT NULL,
    seller_id         BIGINT       NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Payments
CREATE TABLE IF NOT EXISTS payments (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT        NOT NULL UNIQUE,
    status          ENUM('SUCCESS','FAILED','PENDING') NOT NULL,
    amount          DECIMAL(12,2) NOT NULL,
    payment_method  VARCHAR(50),
    transaction_ref VARCHAR(100),
    created_at      DATETIME,
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Wishlist items
CREATE TABLE IF NOT EXISTS wishlist_items (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    added_at   DATETIME,
    UNIQUE KEY uq_wishlist (user_id, product_id),
    CONSTRAINT fk_wishlist_user    FOREIGN KEY (user_id)    REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_wishlist_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Reviews
CREATE TABLE IF NOT EXISTS reviews (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id          BIGINT NOT NULL,
    buyer_id            BIGINT NOT NULL,
    order_id            BIGINT,
    rating              INT    NOT NULL,
    title               VARCHAR(255),
    body                TEXT,
    is_verified_purchase TINYINT(1) DEFAULT 0,
    created_at          DATETIME,
    CONSTRAINT fk_review_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_buyer   FOREIGN KEY (buyer_id)   REFERENCES users(id)
);

-- Seed default categories
INSERT IGNORE INTO categories (id, name, slug, level) VALUES
    (1,  'Electronics',     'electronics',     0),
    (2,  'Fashion',         'fashion',         0),
    (3,  'Home & Garden',   'home-garden',     0),
    (4,  'Sports & Outdoors','sports-outdoors', 0),
    (5,  'Books',           'books',           0),
    (6,  'Toys & Games',    'toys-games',      0),
    (7,  'Health & Beauty', 'health-beauty',   0),
    (8,  'Automotive',      'automotive',      0),
    (9,  'Other',           'other',           0);
