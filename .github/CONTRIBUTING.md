# Contributing to Refined Storage

## Versioning

This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Pull requests

- Keep your PR as small as possible, this makes reviewing easier.
- Commits serve a clear purpose and have a fitting commit message.
- Branches are kept up to date by rebasing, preferably.
- PRs are merged by rebasing the commits on top of the target branch.
- Changes are added in `CHANGELOG.md`. Please refrain from using technical terminology, keep it user-friendly. The
  format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## Gitflow

This project uses [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

## Releasing

1) Make sure the version number in `build.gradle` is correct.
2) Merge `develop` to `main`.
3) Push a tag with the version number (prefixed with `v`).

After releasing:

1) Rename the "Unreleased" section to the correct version number in `CHANGELOG.md`.
2) Upgrade the version number in `build.gradle`.
3) Create a new "Unreleased" section in `CHANGELOG.md`.

## Pipelines

### Build

The build pipeline triggers when a commit is pushed to a branch or pull request.

All tests are run and an aggregated code coverage report is created. After that, a SonarQube analysis is run.

### Release

The release pipeline triggers when a tag is pushed. This will run all the steps that our build pipeline does.

After that succeeds, it will publish to GitHub packages.

The "Unreleased" section in `CHANGELOG.md` is parsed and a GitHub release is created with the changelog body and
relevant artifacts.

After that, a Discord and Twitter notification is sent.
