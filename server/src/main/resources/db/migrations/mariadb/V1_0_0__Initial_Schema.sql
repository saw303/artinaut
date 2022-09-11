CREATE TABLE artifact (
    id UUID NOT NULL,
    version          INTEGER      NOT NULL,
    artifact_id      VARCHAR(50)  NOT NULL,
    artifact_version VARCHAR(50)  NOT NULL,
    group_id         VARCHAR(150) NOT NULL,
    media_type       VARCHAR(100) NOT NULL,
    type             VARCHAR(20)   NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

CREATE TABLE local_repository (
    id UUID NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

CREATE TABLE remote_repository (
    pwd                     VARCHAR(50),
    url_path                VARCHAR(255) NOT NULL,
    store_artifacts_locally BIT          NOT NULL,
    url                     VARCHAR(100) NOT NULL,
    username                VARCHAR(50),
    id UUID NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

CREATE TABLE repo_to_artifacts (
    artifact_id UUID NOT NULL,
    repo_id UUID NOT NULL,
    PRIMARY KEY (artifact_id, repo_id)
)
    ENGINE = InnoDB;

CREATE TABLE repository (
    id UUID NOT NULL,
    version          INTEGER     NOT NULL,
    handle_releases  BIT         NOT NULL,
    handle_snapshots BIT         NOT NULL,
    repo_key         VARCHAR(20) NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

CREATE TABLE virtual_repo_to_repos (
    virtual_repo_id UUID NOT NULL,
    repo_id UUID NOT NULL,
    order_index INTEGER NOT NULL,
    PRIMARY KEY (virtual_repo_id, order_index)
)
    ENGINE = InnoDB;

CREATE TABLE virtual_repository (
    id UUID NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = InnoDB;

ALTER TABLE repository
ADD CONSTRAINT UK_ev7ufgtgqly6i913y5n3wkish UNIQUE (repo_key);

ALTER TABLE local_repository
ADD CONSTRAINT FKqokywl4evunypef3ikt6jjvju
    FOREIGN KEY (id)
        REFERENCES repository(id);

ALTER TABLE remote_repository
ADD CONSTRAINT FKlvxvy1546c2rcm9maup1xtsv6
    FOREIGN KEY (id)
        REFERENCES repository(id);

ALTER TABLE repo_to_artifacts
ADD CONSTRAINT FKtfepc6ds6i9dfaxe2m3cnx5fi
    FOREIGN KEY (repo_id)
        REFERENCES repository(id);

ALTER TABLE repo_to_artifacts
ADD CONSTRAINT FKnpku1f2okc69rnjjjycw86tef
    FOREIGN KEY (artifact_id)
        REFERENCES artifact(id);

ALTER TABLE virtual_repo_to_repos
ADD CONSTRAINT FKg14j5ywmt37ci4p883l9orw9c
    FOREIGN KEY (repo_id)
        REFERENCES repository(id);

ALTER TABLE virtual_repo_to_repos
ADD CONSTRAINT FK9nx3mvmbi3vkr4hls8k00bfmj
    FOREIGN KEY (virtual_repo_id)
        REFERENCES virtual_repository(id);

ALTER TABLE virtual_repository
ADD CONSTRAINT FK4k3v61x6a6cghag7sele55ka6
    FOREIGN KEY (id)
        REFERENCES repository(id);
