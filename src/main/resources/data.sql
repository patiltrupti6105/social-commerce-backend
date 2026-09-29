INSERT IGNORE INTO categories (id, name, slug, parent_id, level) VALUES
(1, 'Fashion', 'fashion', NULL, 0),
(2, 'Electronics', 'electronics', NULL, 0),
(3, 'Beauty', 'beauty', NULL, 0),
(4, 'Home & Living', 'home-living', NULL, 0),
(5, 'Sports', 'sports', NULL, 0),
(6, 'Men''s Clothing', 'mens-clothing', 1, 1),
(7, 'Women''s Clothing', 'womens-clothing', 1, 1),
(8, 'Footwear', 'footwear', 1, 1),
(9, 'Smartphones', 'smartphones', 2, 1),
(10, 'Laptops', 'laptops', 2, 1),
(11, 'Skincare', 'skincare', 3, 1),
(12, 'Makeup', 'makeup', 3, 1),
(13, 'Furniture', 'furniture', 4, 1);
