-- ✅ 카테고리 추가
INSERT INTO p_category (id, name, hidden)
VALUES ('33333333-3333-3333-3333-333333333333', '한식', false)
    ON CONFLICT (id) DO NOTHING;

-- ✅ 테스트 유저 추가
INSERT INTO p_users (id, username, email, password, phone, role, is_deleted)
VALUES
    (1, 'testuser1', 'test1@email.com', '$2a$10$C1nUZfaWYJnbAlA9.7Unb.w6Rpzdb681klizH4DfGkNLJ.O4VEK86', '01012345671', 'CUSTOMER', false),
    (2, 'testuser2', 'test2@email.com', '$2a$10$KxadD4hddUbCtVdHUD9BI.LRps9S6SPo1gCvgE8bm9L3r6miAHFG.', '01012345672', 'CUSTOMER', false),
    (3, 'testuser3', 'test3@email.com', '$2a$10$kVaGnqAZqR3DPC6lGekZBO05twS7GJAFlZpsOdvq5TQBy4MS8Me2m', '01012345673', 'MANAGER', false),
    (4, 'testuser4', 'test4@email.com', '$2a$10$zm/6.0PKF5hy0pUepYIlj.Vm5z1.hkmNRYohLfVXqVnr9foc2r4YO', '01012345674', 'CUSTOMER', false),
    (5, 'testuser5', 'test5@email.com', '$2a$10$6YPA8H7P7Gck4fhUHrqkSuRzSbeSUJfsPQnH85KWSMA.gigiG/tq.', '01012345675', 'OWNER', false),
    (6, 'testuser6', 'test6@email.com', '$2a$10$x.ugubpXMVYFC6YSxRP9LOa0E8sWXSY2lKVYJWaprJNRH1ZfHu7La', '01012345676', 'OWNER', false),
    (7, 'testuser7', 'test7@email.com', '$2a$10$MtEjSjfpwIx31ivsk41CFe9wT8OgG.eWJgfqjvBAFs8oA1J3y5zPi', '01012345677', 'OWNER', false),
    (8, 'testuser8', 'test8@email.com', '$2a$10$5MA.givZ3W4q0vQaBQixkOuSw12mOvmKKSmLFck3gZ8anbP5HpN3C', '01012345678', 'CUSTOMER', false),
    (9, 'testuser9', 'test9@email.com', '$2a$10$WzInLhOfrWInUXcRgMEpk.bDp1RiCN84asmZN6Nbu3tIKUWKW64dm', '01012345679', 'MANAGER', false),
    (10, 'adminuser', 'admin@email.com', '$2a$10$ZQblbHgZkNpqxyKhC0/VW.0vT/rv6I09y7ltgB2NNXL56dB.C0IFC', '01012345670', 'MASTER', false)
    ON CONFLICT (id) DO NOTHING;

-- ✅ RefreshToken 추가
INSERT INTO p_refreshtoken (username, refresh_token, expiry_date)
VALUES
    ('testuser1', 'refreshToken1', NOW() + INTERVAL '7 days'),
    ('testuser2', 'refreshToken2', NOW() + INTERVAL '7 days'),
    ('testuser3', 'refreshToken3', NOW() + INTERVAL '7 days'),
    ('testuser4', 'refreshToken4', NOW() + INTERVAL '7 days'),
    ('testuser5', 'refreshToken5', NOW() + INTERVAL '7 days')
    ON CONFLICT (username) DO UPDATE SET
    refresh_token = EXCLUDED.refresh_token,
    expiry_date = EXCLUDED.expiry_date;

-- ✅ 가게 데이터 추가
INSERT INTO p_store (id, category_id, user_id, name, address, phone, open_time, close_time, hidden)
VALUES
    ('11111111-1111-1111-1111-111111111111', '33333333-3333-3333-3333-333333333333', 5, '테스트 가게', '서울시 강남구', '02-1234-5678', '09:00', '22:00', false)
ON CONFLICT (id) DO NOTHING;

-- ✅ 음식 데이터 추가
INSERT INTO p_food (id, store_id, user_id, name, description, price, image, hidden)
VALUES
    ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111', 5, '테스트 음식 1', '맛있는 음식', 10000, 'food1.jpg', false)
    ON CONFLICT (id) DO NOTHING;

-- ✅ 주문 데이터 추가
INSERT INTO p_orders (order_id, store_id, user_id, phone, address_id, request, total_price, status, review_id, created_at, updated_at)
VALUES
    ('0a6bc8af-fd50-4dad-8265-252fde4b9882', '11111111-1111-1111-1111-111111111111', 1, '010-1111-2222', '55555555-5555-5555-5555-555555555555', '빨리 배달해주세요!', 30000, 'CONFIRMED', null, NOW(), NOW())
ON CONFLICT (order_id) DO NOTHING;

-- ✅ 주문 아이템 추가
INSERT INTO p_order_items (order_item_id, order_id, food_id, food_name, quantity, price)
VALUES
    ('44444444-4444-4444-4444-444444444444', '0a6bc8af-fd50-4dad-8265-252fde4b9882', '22222222-2222-2222-2222-222222222222', '테스트 음식 1', 2, 10000),
    ('55555555-5555-5555-5555-555555555555', '0a6bc8af-fd50-4dad-8265-252fde4b9882', '33333333-3333-3333-3333-333333333333', '테스트 음식 2', 1, 20000)
ON CONFLICT (order_item_id) DO NOTHING;