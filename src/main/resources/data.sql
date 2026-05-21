INSERT INTO theme (name, description, thumbnail_url)
VALUES ('미술관의 밤', '고요한 미술관에서 단서를 모아 탈출하는 추리 테마', 'https://example.com/themes/museum-night.png'),
       ('심해 연구소', '해저 연구소의 사고 원인을 추적하는 SF 테마', 'https://example.com/themes/deep-sea-lab.png'),
       ('폐병원 탈출', '버려진 병원에서 탈출하는 공포 테마', 'https://example.com/themes/hospital.png'),
       ('한밤의 서점', '비밀 통로가 숨겨진 서점에서 단서를 수집하는 미스터리 테마', 'https://example.com/themes/bookstore.png'),
       ('빙하 기지', '얼어붙은 관측 기지에서 생존 루트를 찾는 서바이벌 테마', 'https://example.com/themes/glacier-base.png'),
       ('황금 사원', '사원의 봉인을 해제하고 보물을 찾는 어드벤처 테마', 'https://example.com/themes/golden-temple.png'),
       ('달 기지 탈출', '산소가 부족한 달 기지에서 귀환선을 가동하는 SF 테마', 'https://example.com/themes/moon-base.png'),
       ('무도회장의 유령', '유령이 남긴 암호를 풀어야 하는 고딕 추리 테마', 'https://example.com/themes/ballroom-ghost.png'),
       ('사막 열차', '사막 한가운데 멈춘 열차에서 범인을 찾는 추리 테마', 'https://example.com/themes/desert-train.png'),
       ('해커의 방', '침입당한 서버룸에서 시스템을 복구하는 현대 스릴러 테마', 'https://example.com/themes/hacker-room.png');

INSERT INTO reservation_time (start_at)
VALUES ('10:00:00'),
       ('11:30:00'),
       ('13:00:00'),
       ('14:30:00');

INSERT INTO reservation (name, date, time_id, theme_id)
VALUES ('브라운', DATE '2026-05-05', 1, 1),
       ('코니', DATE '2026-05-04', 1, 1),
       ('샐리', DATE '2026-05-03', 2, 1),
       ('문', DATE '2026-05-02', 2, 1),
       ('제시카', DATE '2026-05-01', 1, 1),

       ('제임스', DATE '2026-05-05', 3, 2),
       ('레오', DATE '2026-05-04', 3, 2),
       ('루카', DATE '2026-05-03', 4, 2),
       ('앤디', DATE '2026-05-02', 4, 2),

       ('레너드', DATE '2026-05-05', 1, 3),
       ('초코', DATE '2026-05-04', 1, 3),
       ('브이', DATE '2026-05-03', 1, 3),

       ('에디', DATE '2026-05-05', 2, 4),
       ('리아', DATE '2026-05-04', 2, 4),

       ('마크', DATE '2026-05-05', 3, 5),
       ('니나', DATE '2026-05-04', 3, 5),

       ('제이', DATE '2026-05-05', 4, 6),
       ('하나', DATE '2026-05-05', 1, 7),
       ('오웬', DATE '2026-05-05', 2, 8),
       ('소라', DATE '2026-05-05', 3, 9),
       ('태오', DATE '2026-05-05', 4, 10),

       ('미래예약1', DATE '2026-05-12', 1, 1),
       ('미래예약2', DATE '2026-05-12', 3, 2);
