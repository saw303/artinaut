SET @mavenCentralId = UUID();

INSERT INTO repository(id, version, handle_releases, handle_snapshots, repo_key)
VALUES (@mavenCentralId, 0, b'1', b'1', 'test-remote');

INSERT INTO remote_repository(id, url, url_path, store_artifacts_locally)
VALUES (@mavenCentralId, 'http://localhost:8090/', 'repositories', b'1');

SET @mavenCentralId = UUID();

INSERT INTO repository(id, version, handle_releases, handle_snapshots, repo_key)
VALUES (@mavenCentralId, 0, b'1', b'1', 'test-local');

INSERT INTO local_repository(id)
VALUES (@mavenCentralId);
