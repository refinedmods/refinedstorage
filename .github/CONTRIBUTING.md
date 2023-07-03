# Contributing

When contributing to this repository, please first discuss the change you wish to make via
[GitHub issues](https://github.com/refinedmods/refinedstorage/issues), [Discord](https://discordapp.com/invite/VYzsydb),
or any other method with the owners of this repository before making a change.

## Pull requests

- Keep your pull request (PR) as small as possible, this makes reviewing easier.
- Commits serve a clear purpose and have a fitting commit message.
- Branches are kept up to date by rebasing (updating a branch by merging makes for a confusing Git history).
- PRs are merged by merging the commits on top of the target branch (which is `develop`).
- Remember to add your changes in `CHANGELOG.md`. If your changes are merely technical, it's not necessary to update the
  changelog as it's not relevant for users.

### Commit messages

Commit messages must adhere to [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/). We
use [Commitlint](https://commitlint.js.org/) to validate commit messages.

We use
the [conventional configuration](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional)
for Commitlint.

It is recommended to install
the [Conventional Commit plugin](https://plugins.jetbrains.com/plugin/13389-conventional-commit) to make it
easier to write commit messages.

### Branch names

Because we use merge commits when merging a PR, branch names will be part of the history of the repository. That is why
branch names must follow a certain standard.

The format is `{category}/GH-{issue number}/{lowercase-description}` and a branch name can be maximum 50 characters of
length.

Category must match a
category [used in our Commitlint config](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional#type-enum).

Valid examples are:

- `fix/GH-123/add-branch-linting`
- `docs/GH-123/cleanup`

## Versioning

This project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

### Version metadata

The code doesn't contain version metadata: `build.gradle` specifies a version of `0.0.0` (via Refined Architect).
The versioning information is entirely contained in Git by using tags.

Per [Semantic Versioning](https://semver.org/spec/v2.0.0.html), the version number being released depends on the changes
in that release. We usually can't predict those
changes at the start of a release cycle, so we can't bump the version at the start of a release cycle. That means that
the version number being released is determined at release time.

Because the version number is determined at release time, we can't store any versioning metadata in the
code (`build.gradle`). If we did, `build.gradle` would have the version number of the latest released version during the
release cycle of the new version, which isn't correct.

### Dealing with Minecraft

Whenever we port to a new Minecraft version, at least the minor version should be incremented.

This is needed so that we can still support older Minecraft versions without the version numbers conflicting.

## Changelog

The changelog is kept in `CHANGELOG.md`.

Keeping a readable, relevant and user-friendly changelog is essential for our end users
to stay up to date with the project.

Please refrain from using technical terminology or adding entries for technical changes
that are (generally) not relevant to the end-user.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## Gitflow

This project uses [Gitflow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow).

## Documentation

Documentation must be kept up to date when adding or changing functionality.

### Javadoc

Javadoc is available after every release on https://refinedmods.com/refinedstorage/.

### API annotations

Public APIs must be annotated with an `@API` annotation
from [API Guardian](https://github.com/apiguardian-team/apiguardian).

## Code style

We use [Checkstyle](https://checkstyle.sourceforge.io/) in our build workflow to validate coding style.

It is recommended to import the [config/checkstyle/checkstyle.xml](../config/checkstyle/checkstyle.xml) file into your
IDE, so that formatting rules are respected.

Moreover, the [CheckStyle-IDEA plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) can be used to check
if there are no style violations.

## Release process

The release process is automated and follows Gitflow.

Before running the "Draft release" workflow to start the release process make sure `CHANGELOG.md` contains all the
unreleased changes.

To determine the version number to be released, the workflow will ask you which release type this is (major, minor,
patch).
The latest version from `CHANGELOG.md` will be used as a base, and that will be incremented
depending on the release type.

`CHANGELOG.md` will be updated by this workflow, you can review this in the resulting release PR.

If you merge the release PR, the "Publish release" workflow will automatically publish the release. An additional PR
will be created to merge the changes in `CHANGELOG.md` back into `develop`.

## Hotfix process

The hotfix process is semi-automated and follows Gitflow:

- Create a hotfix branch off `main`.
- Commit your changes on this branch.
- Update `CHANGELOG.md` (with version number and release date) manually on this branch.
- Push the branch and create a PR for it, merging into `main`.

The "Publish release" workflow will take care of the rest.

## Workflows

We have a few GitHub workflows:

- Build (PRs, pushes to `develop` and `main`)
- Draft release (manual trigger)
- Publish release (merging a PR to `main`)
- Validate changelog (PRs)
  - To validate if `CHANGELOG.md` is valid and updated.
  - Not every pull request needs a changelog change, so the `skip-changelog` label can be added to the pull request to
    ignore this.
- Validate commit messages (PRs)
  - Validates whether the commits on a pull request
    respect [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/).
  - We use
    the [conventional configuration](https://github.com/conventional-changelog/commitlint/tree/master/%40commitlint/config-conventional).
- Validate branch names (PRs)
- Issue for unsupported version (issues)
  - Posts a message on a GitHub issue if the issue is about an unsupported version.
- Lock resolved issues and PRs (every night)

### Build

The build workflow triggers when a pull request is updated or when a commit is pushed to `develop` or `main`.

The build workflow takes care of the following:

- Running a Gradle build, running our tests in the process and generating an aggregated code coverage report for the API
  modules.
- Analyzing the code on SonarQube.
  > Because of
  > [limitations with SonarQube](https://portal.productboard.com/sonarsource/1-sonarcloud/c/50-sonarcloud-analyzes-external-pull-request),
  > pull requests originating from a fork aren't analyzed on SonarQube.

- Code style validation with Checkstyle.
- Mutation and line coverage test with Pitest.
- Uploading the artifacts on the action.

### Draft release

The draft release workflow is a manual workflow which will create a release branch from `develop`.

To determine the version number to be released, it will extract the latest version number from `CHANGELOG.md` and
increment it depending on the release type selected.

This workflow takes care of the following:

- Creating the release branch.
- Updating the changelog on this release branch.
- Creating a pull request merging the release branch into `main`.

### Publish release

The "publish release" workflow is triggered when a release or hotfix PR is merged to `main`. Usually, this will be the
PR created earlier in the "Draft release" workflow.

The workflow takes care of the following:

- Extracting the version number from the release or hotfix branch name that is merged in the PR.
- Extracting the changelog entry for this version number.
- Running a build.
- Publishing on [GitHub packages](https://github.com/refinedmods/refinedstorage/packages) and
  CreeperHost Maven.
- Publishing Javadoc on [GitHub pages](https://github.com/refinedmods/refinedstorage/tree/gh-pages).
- Deploying on [GitHub releases](https://github.com/refinedmods/refinedstorage/releases).
- Announcing the release on Discord and Twitter.
- Creating a PR that merges `main` back into `develop` to get the changes to `CHANGELOG.md` and `build.gradle`
  into `develop` from the draft release workflow.
