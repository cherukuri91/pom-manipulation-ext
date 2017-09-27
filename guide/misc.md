---
title: Miscellaneous PME Manipulations
---

* Contents
{:toc}

### Overview

In addition to the main [project-version](project-version-manip.html), [dependency](dep-manip.html), and [plugin](plugin-manip.html) manipulations, PME offers several smaller features. These feaures fall into two main categories:

* POM Cleanup
* Build Management

### General

PME will be default scan every profile in the project. To restrict scanning to only those profiles that are active set `scanActiveProfiles` to true.

Note: This will only detect those profiles explicitly activated via -P ; property activation will not be correctly detected. Further if this is being used in a system such as https://github.com/project-ncl/pnc, unless the profiles are explictly propagated via the CLI this option will have no affect.

### POM Cleanup

#### Repository And Reporting Removal

If the property `repoReportingRemoval` (*Deprecated property `repo-reporting-removal`*) is set, PME will remove all reporting and repository sections (including profiles) from the POM files.

Repository declarations in the POM are considered a bad build smell, since over time they may become defunct or move.

If the property `repoRemovalIgnorelocalhost` (*Deprecated property `repo-removal-ignorelocalhost`*) is set (default: false) PME will not remove repositories that contain the following definitions

* file://
* (http or https)://localhost
* (http or https)://127.00.1
* (http or https)://::1

Occasionally a project's more complex example/quickstart may have a local repository definition; this allows those to be preserved.

Additionally, most project rebuilders aren't interested in hosting their own copy of the project's build reports or generated website; therefore, the reporting section only adds more plugin artifacts to the list of what must be present in the environment for the build to succeed. Eliminating this section simplifies the build and reduces the risk of failed builds.

If the property `repoRemovalBackup` (*Deprecated property `repo-removal-backup`*) (default value: off) is set to
* `settings.xml` a backup of any removed sections will be created in the top level directory.
* `<path to file>` a backup of any removed sections will be created in the specified file.

#### `project.version` Expression Replacement

The extension will automatically replace occurences of the property expression `${project.version}` in POMs (of packaging type `pom`).

This avoids a subtle problem that occurs when another project with inherits from this POM. If the child POM (the one that declares the `<parent/>`) specifies its own version **and that version is different from the parent**, that child version will be used to resolve `${project.version}` instead of the intended (parent) version. Resolving these expressions when `packaging` is set to `pom` (the only type of POM that can act as a parent) prevents this from occurring.

This behavior may be configured by setting the property `enforceProjectVersion` (*Deprecated property `enforce-project-version`*):

    -DenforceProjectVersion=on|off

As explained above, the default is `on`.

### Build Management

#### Profile Injection

PME supports injection of profiles declared in a remote POM file. Simply supply a remote management POM:

    mvn install -DprofileInjection=org.foo:profile-injection:1.0

The extension will, for every profile in the remote POM file, replace or add it to the local top level POM file.

**Note:** for any existing profile in the modified POM that specifies `activeByDefault`, this activation option will be removed so profiles are not accidentally disabled due to its exclusive semantics.

#### Profile Removal

PME supports removal of profiles as indicated by a comma separated list of profile IDs.

    mvn install -DprofileRemoval=profileOne,profileTwo

#### Repository Injection

PME supports injection of remote repositories. Supply a remote repository management POM:

	mvn install -DrepositoryInjection=org.foo:repository-injection:1.0

The extension will resolve a remote POM file and inject remote repositories to either the local top level POM file or the POM(s) specified by the property `repositoryInjectionPoms` (which should be in the form of a comma separated list e.g. `org.myproject:mychild`). If there is a local repository with id identical to the injected one, it is overwritten. The `repositoryInjectionPoms` property supports wildcards on the _artifactId_ e.g.

    repositoryInjectionPoms=org.commonjava.maven.ext.wildcard:*


#### Property Override

PME may also be used to override properties prior to interpolating the model. Multiple property mappings can be overridden using a similar pattern to dependencies via a remote property management pom.

    mvn install -DpropertyManagement=org.foo:property-management:10

This will inject the properties at the inheritance root(s). It will also, for every injected property, find any matching property in the project and overwrite its value.

Overriding properties can be a simple, minimalist way of controlling build behavior if the appropriate properties are already defined.