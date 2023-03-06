# Invoke Business-Tests via Maven

```shell
./mvnw -pl edc-tests -Pbusiness-tests -pl edc-tests test -Dtest=org.eclipse.tractusx.edc.tests.features.RunCucumberTest
```

# Test locally using Act Tool

> "Think globally, [`act`](https://github.com/nektos/act) locally"

```shell
act -j business-test
```

# Run and debug Business-Tests local within IDE
Please refer to [run-local documentation in docs](../docs/development/Run-business-tests-local.md)