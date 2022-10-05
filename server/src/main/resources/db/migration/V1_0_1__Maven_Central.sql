SET @mavenCentralId = UUID();

INSERT INTO repository(id, version, handle_releases, handle_snapshots, repo_key)
VALUES (@mavenCentralId, 0, b'1', b'0', 'mavenCentral');

INSERT INTO remote_repository(id, url, url_path, store_artifacts_locally)
VALUES (@mavenCentralId, 'https://repo.maven.apache.org/', 'maven2', b'1');
