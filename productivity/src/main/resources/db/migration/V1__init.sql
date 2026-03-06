CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    first_login BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tasks (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(1000),
    priority VARCHAR(50),
    estimated_minutes INTEGER,
    fixed BOOLEAN,
    fixed_start_time TIME,
    fixed_end_time TIME,
    user_email VARCHAR(255),
    status VARCHAR(50),
    sender VARCHAR(255),
    urgency_score INTEGER,
    importance_score INTEGER,
    final_priority_score INTEGER,
    quadrant VARCHAR(50),
    scope VARCHAR(50),
    due_date TIMESTAMP,
    source_platform VARCHAR(100),
    tags VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_tasks_user_email ON tasks(user_email);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status);

CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_email VARCHAR(255),
    title VARCHAR(255),
    body VARCHAR(1000),
    type VARCHAR(100),
    read BOOLEAN,
    created_at TIMESTAMP,
    task_id BIGINT
);

CREATE INDEX IF NOT EXISTS idx_notifications_user_email ON notifications(user_email);
CREATE INDEX IF NOT EXISTS idx_notifications_read ON notifications(read);

CREATE TABLE IF NOT EXISTS user_profiles (
    id SERIAL PRIMARY KEY,
    email VARCHAR(255),
    name VARCHAR(255),
    profession VARCHAR(255),
    goal VARCHAR(255),
    wake_time VARCHAR(32),
    sleep_time VARCHAR(32),
    dob VARCHAR(32),
    age INTEGER
);

CREATE TABLE IF NOT EXISTS user_priorities (
    user_id BIGINT REFERENCES user_profiles(id) ON DELETE CASCADE,
    priority VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_user_profiles_email ON user_profiles(email);

CREATE TABLE IF NOT EXISTS user_behavior_metrics (
    id SERIAL PRIMARY KEY,
    user_email VARCHAR(255),
    sender VARCHAR(255),
    seen_count BIGINT,
    completed_count BIGINT,
    ignored_count BIGINT,
    last_seen_at TIMESTAMP,
    last_completed_at TIMESTAMP,
    sender_importance_index INTEGER
);

CREATE INDEX IF NOT EXISTS idx_behavior_user_sender ON user_behavior_metrics(user_email, sender);

CREATE TABLE IF NOT EXISTS integration_accounts (
    id SERIAL PRIMARY KEY,
    user_email VARCHAR(255),
    provider VARCHAR(64),
    access_token VARCHAR(2048),
    refresh_token VARCHAR(2048),
    account_label VARCHAR(255)
);

