TRUNCATE permissions, roles, role_hierarchy, role_permissions CASCADE;

INSERT INTO permissions (id, resource, action) VALUES
('users:read', 'users', 'read'),
('users:write', 'users', 'write'),
('users:update', 'users', 'update'),
('users:delete', 'users', 'delete'),
('abilities:read', 'abilities', 'read'),
('abilities:write', 'abilities', 'write'),
('abilities:update', 'abilities', 'update'),
('abilities:delete', 'abilities', 'delete'),
('lessons:read', 'lessons', 'read'),
('lessons:write', 'lessons', 'write'),
('lessons:update', 'lessons', 'update'),
('lessons:delete', 'lessons', 'delete'),
('modules:read', 'modules', 'read'),
('modules:write', 'modules', 'write'),
('modules:update', 'modules', 'update'),
('modules:delete', 'modules', 'delete'),
('subscriptions:read', 'subscriptions', 'read'),
('subscriptions:write', 'subscriptions', 'write'),
('subscriptions:update', 'subscriptions', 'update'),
('subscriptions:delete', 'subscriptions', 'delete');

INSERT INTO roles (rolename) VALUES
('ADMIN'),
('USER');

INSERT INTO role_hierarchy(role, included_role) VALUES
('ADMIN', 'USER');

INSERT INTO role_permissions (role, permission) VALUES
('ADMIN', 'users:read'),
('ADMIN', 'users:write'),
('ADMIN', 'users:update'),
('ADMIN', 'users:delete'),
('ADMIN', 'abilities:read'),
('ADMIN', 'abilities:write'),
('ADMIN', 'abilities:update'),
('ADMIN', 'abilities:delete'),
('ADMIN', 'lessons:read'),
('ADMIN', 'lessons:write'),
('ADMIN', 'lessons:update'),
('ADMIN', 'lessons:delete'),
('ADMIN', 'modules:read'),
('ADMIN', 'modules:write'),
('ADMIN', 'modules:update'),
('ADMIN', 'modules:delete'),
('ADMIN', 'subscriptions:read'),
('ADMIN', 'subscriptions:write'),
('ADMIN', 'subscriptions:update'),
('ADMIN', 'subscriptions:delete'),

('USER', 'lessons:read'),
('USER', 'modules:read');