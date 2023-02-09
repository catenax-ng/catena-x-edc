# Release process of product-edc

## Decision

To improve stability, reproducibility and maintainability of releases, product-edc will henceforth employ a branching
model similar
to [GitFlow](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow#:~:text=What%20is%20Gitflow%3F,lived%20branches%20and%20larger%20commits).
Aside from rolling versions and developer deployments there will be no dependency onto `SNAPSHOT` versions anymore.

Every version published by product-edc must be reproducible at any time.

## Rationale

Depending on snapshot versions of upstream projects, such as EDC, is inherently dangerous, particularly when that
dependency has not yet reached a final state and breaking changes are to be expected. Most problems will stem from
breaking changes, such as Java SPIs, APIs and changes in service contracts.

Up until now, the only way out was cherry-picking, which is extremely cumbersome and error-prone, and requires a
parallel build pipeline to publish the cherry-picked artifacts of EDC (and potentially others).

## Approach

### During development

Features are developed in feature branches and use `-SNAPSHOT` versions of the upstream EDC packages. That means, that
the `develop` branch also uses snapshots. It is assumed that when the build breaks due to changes in upstream, the fix
can be done easily and in a timely manner, much more so than working off technical debt that accumulates over several
months. Builds on `develop` are therefore _not repeatable_, but that downside of this is easily offset by the tighter
alignment with and smaller technical debt and integration pain with the upstream EDC.

### Nightly builds

Nightly builds are generated according to a fixed schedule (as suggested
in [this Jira issue](https://jira.catena-x.net/browse/A1IDSC-408)). Upstream EDC will soon begin to publish nightly
builds as actual releases (as opposed to: snapshots) to a separate OSSRH-operated repository (see
[this decision record](https://github.com/eclipse-edc/Connector/tree/main/docs/developer/decision-records/2023-02-10-nightly-builds)).

Unfortunately there is no reliable way to trigger the product-edc build whenever a new EDC nightly is created, and using
upstream snapshots is dangerous and may lead to unexpected and unreproducible results. The most reliable way is to
periodically query for the latest EDC nightly, e.g. using the method highlighted in
the aforementioned Jira issue:

```shell
curl <OSSHR_RELEASES_REPO_URL>/org/eclipse/edc/connector-core/maven-metadata.xml | xmllint --xpath "//metadata/versioning/versions/version[last()]" -
```

That would return something like to `0.0.1-20230213`. As soon as the version string (here: Feb 13th, 2023) is equal to
the current date, we can start the nightly.

### Creating a release

First, a new branch `releases/X.Y.Z` based off of `develop` is created. This can either be done
on `HEAD`, or - if desired - on a particular ref. The latter case is relevant if there are already features
in `develop` that are not scoped for the release.

Second, the dependency onto EDC is updated to the most recent nightly build. For example, if a release is
created on March 27th 2023, the most recent nightly would be `0.0.1-20230326`. Nightlies in EDC are published around
8am, so `0.0.1-20230327` might not exist yet.

_Updating Gradle files or Maven POMs, creating branches and tags in Git should be automated through Github Actions as
part of the release process. For reference_:

- Modifying and committing files: https://github.com/orgs/community/discussions/26842#discussioncomment-3253612
- Creating branches: https://github.com/marketplace/actions/create-branch
- Creating tags using Github's
  API: https://github.com/eclipse-edc/Connector/blob/b24a5cacbc9fcabdfd8020d779399b3e56856661/.github/workflows/release-edc.yml#L21 (
  example)

### Bugfixes/Hotfixes

Once a release is published, for example `0.3.0` it will receive no further development save for hotfixes. Similarly,
hotfix branches are
created based off of the release branch, here `releases/0.3.0`, thus, `hotfix/0.3.1`. From this, three scenarios emerge:

1. The actual fix is done on `develop` and can be cherry-picked into the `hotfix/0.3.1` branch. No new commits are
   made directly in that branch.
2. The actual fix is done on `develop` and must be manually ported into the `hotfix/0.3.1` branch. One or several new
   commits are made on `hotfix/0.3.1`. This is needed when cherry-picking is not available due to incompatibilities
   between `develop` and the hotfix branch due to intermittent changes.
3. The fix is only relevant for the `0.3.1` hotfix, it is not needed in `develop`. This can happen, when the problem is
   not present on `develop`, because it was already implicitly fixed, or otherwise doesn't exist.

This might produce many branches, and the first `hotfix` makes the release obsolete, but it will greatly help
readability and make a release's history readily apparent.

## Concrete steps

- switch over to EDC nightly builds
- make `develop` use EDC `SNAPSHOT` builds
- update the hotfix flow to use the `hotfix/` prefix
- create stable nightly builds (see [Jira](https://jira.catena-x.net/browse/A1IDSC-408))

## General rules

- Release branches must not change dependency versions, unless there is a clear and concise reason to do so.
- If possible, bugs/defects should be fixed on `develop` and be backported to the respective release branch as hotfix
- Catena-X/Tractus-X will only provide hotfixes for critical security bugs as defined by the committers for the
  currently released version. Nothing else.
- Every release should also trigger a Github Release, which provides an easily readable Changelog and a source package,
  etc. This should be automated as well (EDC uses [this Github Action](https://github.com/ncipollo/release-action)).
- Feature branches are to be created in developers' forks _only_ so as to not pollute the Git reflog of the `origin` too
  much.

## Notes for becoming OpenSource

- All artifacts (docker images, helm charts, Maven artifacts) should be published to well-known and publicly accessible
  locations such as MavenCentral, DockerHub, etc. The GitHub Packages repository is only accessible to authenticated
  users.
- When and if Tractus-X becomes an Eclipse project, we'll have to adopt the Eclipse Foundation's publishing guidelines,
  which prescribes the use of Jenkins for publishing to MavenCentral and OSSRH.
- Typically, GitHub Actions should perform all verification tasks, running tests, etc. and Jenkins' only purpose is to
  publish.