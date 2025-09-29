-- 휴면 전환 대상 (1년 이상 로그인 안함)
INSERT INTO EXAM_USER (username, status, last_login_at) VALUES ('user_old_1', 'ACTIVE', TO_TIMESTAMP('2023-01-10T10:00:00', 'YYYY-MM-DD"T"HH24:MI:SS'));
INSERT INTO EXAM_USER (username, status, last_login_at) VALUES ('user_old_2', 'ACTIVE', TO_TIMESTAMP('2022-05-20T11:00:00', 'YYYY-MM-DD"T"HH24:MI:SS'));

-- 휴면 전환 대상 아님 (최근 로그인)
INSERT INTO EXAM_USER (username, status, last_login_at) VALUES ('user_recent_1', 'ACTIVE', TO_TIMESTAMP('2025-09-20T14:30:00', 'YYYY-MM-DD"T"HH24:MI:SS'));
INSERT INTO EXAM_USER (username, status, last_login_at) VALUES ('user_recent_2', 'ACTIVE', TO_TIMESTAMP('2025-08-01T18:00:00', 'YYYY-MM-DD"T"HH24:MI:SS'));

-- 이미 휴면 상태인 사용자
INSERT INTO EXAM_USER (username, status, last_login_at) VALUES ('user_dormant', 'DORMANT', TO_TIMESTAMP('2020-01-01T00:00:00', 'YYYY-MM-DD"T"HH24:MI:SS'));