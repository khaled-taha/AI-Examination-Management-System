CREATE TABLE groups (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE resource_directory (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    creator VARCHAR(255) NOT NULL,
    base_dir_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE course (
    code VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    avatar_id UUID,
    active BOOLEAN DEFAULT true,
    group_id UUID NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    dir_doc_id UUID NOT NULL,
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (dir_doc_id) REFERENCES resource_directory(id)
);

CREATE TABLE resource (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL,
    resource_dir_id UUID NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (resource_dir_id) REFERENCES resource_directory(id)
);

CREATE TABLE super_resource (
    id UUID PRIMARY KEY,
    data BYTEA NOT NULL,
    resource_id UUID NOT NULL,
    FOREIGN KEY (resource_id) REFERENCES resource(id)
);