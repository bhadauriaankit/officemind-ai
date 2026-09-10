-- V12: Seed default enterprise departments

SET search_path TO officemind, public;

INSERT INTO departments (id, name, description, created_at, updated_at)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'Engineering', 'Software development, DevOps, and technical infrastructure', now(), now()),
    ('20000000-0000-0000-0000-000000000002', 'Human Resources', 'People operations, recruiting, culture, and employee benefits', now(), now()),
    ('20000000-0000-0000-0000-000000000003', 'Finance', 'Financial planning, accounting, expenses, and budgeting', now(), now()),
    ('20000000-0000-0000-0000-000000000004', 'Information Technology', 'Enterprise IT support, hardware, security, and internal systems', now(), now()),
    ('20000000-0000-0000-0000-000000000005', 'Marketing', 'Product marketing, branding, communications, and public relations', now(), now()),
    ('20000000-0000-0000-0000-000000000006', 'Operations', 'Business operations, facilities management, and administrative services', now(), now())
ON CONFLICT (name) DO NOTHING;
