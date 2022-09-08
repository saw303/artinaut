SET @gradleId = UUID();

INSERT INTO repository(id, version, handle_releases, handle_snapshots, repo_key)
VALUES (@gradleId, 0, b'1', b'0', 'gradle-plugins');

INSERT INTO remote_repository(id, url, url_path, store_artifacts_locally)
VALUES (@gradleId, 'https://plugins.gradle.org/', 'm2', b'1');
