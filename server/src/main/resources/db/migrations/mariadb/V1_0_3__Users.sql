CREATE TABLE `group` (
    id UUID NOT NULL,
    version INTEGER     NOT NULL,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

CREATE TABLE `user_groups` (
    user_id UUID NOT NULL,
    `groups_id` UUID NOT NULL,
    PRIMARY KEY (user_id, `groups_id`)
)
    ENGINE = InnoDB;

CREATE TABLE `group_roles` (
    `group_id` UUID NOT NULL,
    roles_id UUID NOT NULL,
    PRIMARY KEY (`group_id`, roles_id)
)
    ENGINE = InnoDB;

CREATE TABLE role (
    id UUID NOT NULL,
    version INTEGER     NOT NULL,
    name    VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

CREATE TABLE user (
    id UUID NOT NULL,
    version  INTEGER     NOT NULL,
    name     VARCHAR(25) NOT NULL,
    password VARCHAR(80) NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

ALTER TABLE `user_groups`
ADD CONSTRAINT UK_jrc4ri7j11xq29b6p2unbmn5n UNIQUE (`groups_id`);

ALTER TABLE `group`
ADD CONSTRAINT UK_oy92ak6u4rmbq75jgb14npht7 UNIQUE (name);

ALTER TABLE `group_roles`
ADD CONSTRAINT UK_qd6tnvt3jgb9q3f6ot4xvrrw9 UNIQUE (roles_id);

ALTER TABLE role
ADD CONSTRAINT UK_8sewwnpamngi6b1dwaa88askk UNIQUE (name);

ALTER TABLE user
ADD CONSTRAINT UK_gj2fy3dcix7ph7k8684gka40c UNIQUE (name);

ALTER TABLE `user_groups`
ADD CONSTRAINT FK3b9mwb4emq02yrg6rnwlchdvn
    FOREIGN KEY (`groups_id`)
        REFERENCES `group`(id);

ALTER TABLE `user_groups`
ADD CONSTRAINT FK9k141qbhyu06g0hk0dk0uhjgj
    FOREIGN KEY (user_id)
        REFERENCES user(id);

ALTER TABLE `group_roles`
ADD CONSTRAINT FK12l023hjj7euholq5rsloxeyy
    FOREIGN KEY (roles_id)
        REFERENCES role(id);

ALTER TABLE `group_roles`
ADD CONSTRAINT FK7v3il552anu8tomrxxt1jw1p4
    FOREIGN KEY (`group_id`)
        REFERENCES `group`(id);

SET @adminRoleId = (SELECT UUID());
SET @adminName = (SELECT 'ADMIN');

INSERT INTO role(id, version, name)
VALUES (@adminRoleId, 0, @adminName);

SET @adminGroupId = (SELECT UUID());

INSERT INTO `group`(id, version, name)
VALUES (@adminGroupId, 0, @adminName);

INSERT INTO group_roles(group_id, roles_id)
VALUES (@adminGroupId, @adminRoleId);
