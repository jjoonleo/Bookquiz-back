-- Insert default authorities (roles)
INSERT INTO authorities (name, description, created_at)
VALUES 
    ('ROLE_USER', 'Default user role with basic permissions', NOW()),
    ('ROLE_ADMIN', 'Administrator role with full system access', NOW())
ON CONFLICT (name) DO NOTHING;
