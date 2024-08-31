-- User 데이터 삽입
INSERT INTO p_users (id, username, nickname, email, password, role, created_at) VALUES
                                                                                    (1, 'john_doe', 'johnny', 'john@example.com', 'hashed_password1', 'CUSTOMER', NOW()),
                                                                                    (2, 'jane_smith', 'janes', 'jane@example.com', 'hashed_password2', 'OWNER', NOW()),
                                                                                    (3, 'alice_wonder', 'alice', 'alice@example.com', 'hashed_password3', 'CUSTOMER', NOW()),
                                                                                    (4, 'bob_builder', 'bob', 'bob@example.com', 'hashed_password4', 'MANAGER', NOW()),
                                                                                    (5, 'charlie_chaplin', 'charlie', 'charlie@example.com', 'hashed_password5', 'CUSTOMER', NOW());

-- Region 데이터 삽입
INSERT INTO p_regions (id, region_name) VALUES
                                            ('11111111-1111-1111-1111-111111111111', 'Springfield'),
                                            ('22222222-2222-2222-2222-222222222222', 'Metropolis'),
                                            ('33333333-3333-3333-3333-333333333333', 'Gotham City');

-- Store 데이터 삽입
INSERT INTO p_stores (id, user_id, region_id, store_name, store_img, created_at) VALUES
                                                                                     ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 2, '11111111-1111-1111-1111-111111111111', 'Jane''s Bakery', 'image_url_bakery', NOW()),
                                                                                     ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 2, '22222222-2222-2222-2222-222222222222', 'Jane''s Coffee Shop', 'image_url_coffee', NOW()),
                                                                                     ('cccccccc-cccc-cccc-cccc-cccccccccccc', 4, '33333333-3333-3333-3333-333333333333', 'Bob''s Hardware', 'image_url_hardware', NOW()),
                                                                                     ('dddddddd-dddd-dddd-dddd-dddddddddddd', 5, '11111111-1111-1111-1111-111111111111', 'Charlie''s Pizza Place', 'image_url_pizza', NOW()),
                                                                                     ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 3, '22222222-2222-2222-2222-222222222222', 'Alice''s Wonderland', 'image_url_wonderland', NOW());

-- Menu 데이터 삽입
INSERT INTO p_menus (id, store_id, menu_name, price, menu_img, created_at) VALUES
                                                                               ('aaaa1111-1111-1111-1111-111111111111', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Chocolate Cake', 15000, 'chocolate_cake_image_url', NOW()),
                                                                               ('aaaa2222-2222-2222-2222-222222222222', 'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Red Velvet Cake', 18000, 'red_velvet_cake_image_url', NOW()),
                                                                               ('bbbb1111-1111-1111-1111-111111111111', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Espresso', 3000, 'espresso_image_url', NOW()),
                                                                               ('bbbb2222-2222-2222-2222-222222222222', 'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Cappuccino', 4000, 'cappuccino_image_url', NOW()),
                                                                               ('dddd1111-1111-1111-1111-111111111111', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Margherita Pizza', 12000, 'margherita_pizza_image_url', NOW()),
                                                                               ('dddd2222-2222-2222-2222-222222222222', 'dddddddd-dddd-dddd-dddd-dddddddddddd', 'Pepperoni Pizza', 14000, 'pepperoni_pizza_image_url', NOW()),
                                                                               ('cccc1111-1111-1111-1111-111111111111', 'cccccccc-cccc-cccc-cccc-cccccccccccc', 'Hammer', 7000, 'hammer_image_url', NOW()),
                                                                               ('eeee1111-1111-1111-1111-111111111111', 'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', 'Magic Tea', 5000, 'magic_tea_image_url', NOW());

-- Address 데이터 삽입
INSERT INTO p_addresses (id, user_id, address_name, request, created_at) VALUES
                                                                             ('aaaa0000-0000-0000-0000-000000000001', 1, '123 Main St, Springfield', 'Leave at the door', NOW()),
                                                                             ('aaaa0000-0000-0000-0000-000000000002', 2, '456 Elm St, Metropolis', 'Ring the bell', NOW()),
                                                                             ('aaaa0000-0000-0000-0000-000000000003', 3, '789 Oak St, Gotham City', 'Please be quick', NOW()),
                                                                             ('aaaa0000-0000-0000-0000-000000000004', 4, '101 Maple St, Springfield', 'Call on arrival', NOW()),
                                                                             ('aaaa0000-0000-0000-0000-000000000005', 5, '202 Pine St, Metropolis', 'Handle with care', NOW());