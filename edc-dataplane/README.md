# Data Plane

The Eclipse Dataspace Connector consists of a **Control Plan** and a **Data Plane** Application.
While the **Control Plane** managing several data transfers, the **Control Plane** is responsible for doing the actual transfer. Like this data is never routed through the control plane itself und must always pass the data plane.

## Security

### Confidential Settings

Please be aware that there are several confidential settings, that should not be part of the actual EDC configuration file (e.g. the Vault credentials).

As it is possible to configure EDC settings via environment variables, one way to do it would be via Kubernetes Secrets. For other deployment scenarios than Kubernetes equivalent measures should be taken.

# Known Data Plane Issues
Please have a look at the open issues in the open source repository. The list below might not be maintained well and
only contains the most important issues.
EDC Github Repository https://github.com/eclipse-dataspaceconnector/DataSpaceConnector/issues

---

**Please note** that some of these issues might already be fixed on the EDC main branch, but are not part of the specific
EDC commit the Product-EDC uses.

---

[empty]