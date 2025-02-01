-- Insert fake groups
INSERT INTO groups (id, name, description) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'Group A', 'Description for Group A'),
('550e8400-e29b-41d4-a716-446655440001', 'Group B', 'Description for Group B')
ON CONFLICT (id)
DO NOTHING;

-- Insert fake resource directories
INSERT INTO resource_directory (id, name, creator, base_dir_id, created_at, updated_at) VALUES
('550e8400-e29b-41d4-a716-446655440002', 'Directory 1', 'Admin', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440003', 'Directory 2', 'Admin', NULL, NOW(), NOW()),
('550e8400-e29b-41d4-a716-446655440004', 'Directory 1.1', 'Admin', '550e8400-e29b-41d4-a716-446655440002', NOW(), NOW())
ON CONFLICT (id)
DO NOTHING;

-- Insert fake courses
INSERT INTO course (code, name, avatar_id, active, group_id, created_at, updated_at, dir_doc_id) VALUES
('C001', 'Course 1', '550e8400-e29b-41d4-a716-446655440004', true, '550e8400-e29b-41d4-a716-446655440000', NOW(), NOW(), '550e8400-e29b-41d4-a716-446655440002'),
('C002', 'Course 2', '550e8400-e29b-41d4-a716-446655440005', true, '550e8400-e29b-41d4-a716-446655440001', NOW(), NOW(), '550e8400-e29b-41d4-a716-446655440003')
ON CONFLICT (code)
DO NOTHING;