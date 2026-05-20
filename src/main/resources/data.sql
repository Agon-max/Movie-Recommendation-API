-- Auto-run by Spring Boot after Hibernate creates the schema.
-- Controlled by `spring.sql.init.mode=always` in application.properties.
--
-- Seeds a catalogue of rewards spanning every point tier. Names are chosen
-- to match the icon-keyword map in the frontend RewardCard so each tile
-- gets a distinctive icon.

INSERT INTO rewards (name, description, point_cost, type, monetary_value, stock, active) VALUES
    -- Starter tier (under 50 pts)
    ('Popcorn Voucher',                   'Free large popcorn at any partnered theater. The classic snack.',   25, 'GIFT_CARD',     4.00,  500, TRUE),
    ('Skip-the-Ads 24h Pass',             'One full day of ad-free streaming on MovieRec.',                    30, 'DISCOUNT_CODE', 0.00,  1000, TRUE),
    ('Snack Combo Coupon',                'Popcorn + soda combo at participating cinemas.',                    45, 'DISCOUNT_CODE', 6.00,  300, TRUE),

    -- Mid tier (50-150 pts)
    ('Movie Ticket Discount',             '$5 off a single movie ticket at any AMC or Cinemark.',              75, 'DISCOUNT_CODE', 5.00,  250, TRUE),
    ('$10 Coffee Card',                   'Pre-loaded coffee card good at any major chain.',                  100, 'GIFT_CARD',    10.00,  200, TRUE),
    ('Premium Trailer Pack',              'Unlock exclusive behind-the-scenes content for any movie.',        120, 'DISCOUNT_CODE', 0.00,  999, TRUE),
    ('$10 Movie Cinema Gift Card',        'Spendable at the snack bar or box office. Stackable.',             150, 'GIFT_CARD',    10.00,  150, TRUE),

    -- Higher tier (150-300 pts)
    ('Streaming Service Credit ($15)',    'Apply to any major streaming subscription.',                       200, 'GIFT_CARD',    15.00,  100, TRUE),
    ('$25 Cinema Gift Card',              'Two tickets + snacks at any partnered theater.',                   250, 'GIFT_CARD',    25.00,  80,  TRUE),
    ('$15 Cash Payout',                   'Instant cash payout to PayPal or bank transfer.',                  280, 'CASH_PAYOUT',  15.00,  200, TRUE),

    -- Premium tier (300-600 pts)
    ('VIP Movie Night Bundle',            '2 premium tickets, popcorn combo, and reserved seating.',          400, 'GIFT_CARD',    60.00,  40,  TRUE),
    ('$25 Cash Payout',                   'Direct cash payout. The flexible reward.',                         450, 'CASH_PAYOUT',  25.00,  100, TRUE),
    ('$50 Streaming Gift Card',           'Use on Netflix, Hulu, Disney+ or HBO Max.',                        500, 'GIFT_CARD',    50.00,  50,  TRUE),
    ('Music Streaming Voucher',           '3 months of premium music streaming.',                             550, 'GIFT_CARD',    30.00,  75,  TRUE),

    -- Elite tier (700+ pts)
    ('VIP Premium Cinema Pass',           'Unlimited movies for a month at participating chains.',            750, 'GIFT_CARD',   100.00,  20,  TRUE),
    ('Headphones Discount Code',          '40% off premium over-ear headphones from our partner.',            800, 'DISCOUNT_CODE',80.00,  50,  TRUE),
    ('$50 Cash Payout',                   'The big payout. Direct deposit, no strings.',                      900, 'CASH_PAYOUT',  50.00,  30,  TRUE),
    ('Gaming Console Discount',           '$100 off a next-gen console at our retail partner.',              1200, 'DISCOUNT_CODE',100.00, 15,  TRUE),
    ('VIP Trophy Bundle',                 'Limited edition collectible trophy + signed poster.',             1500, 'GIFT_CARD',   150.00, 10,  TRUE),
    ('$100 Cash Payout',                  'Top-tier payout. For the dedicated cinephile.',                   2000, 'CASH_PAYOUT', 100.00,  10,  TRUE);
