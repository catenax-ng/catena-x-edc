# Software Operation View

## Introduction

The following documentation will guide you through the TractusX deployment.
You will be setting up multiple controllers and enabling communication between them.
<!---
move the following to main README.md
-->
> ### TractusX EDC or Core EDC?
> 
> The following guide assumes the use of the TractusX EDC.
> It includes the Core EDC with all of its functionality.
> However, this core is supplemented by extensions that allow for the use of additional backends and connection types.
> Furthermore, the provided Helm charts, build configuration and tests allow for a smoother deployment.

## Connector Components
<!---
move the following to main README.md
-->
In a usual EDC environment, each participant would operate at least one connector.
Each of these connectors consists of a control plane and a data plane.
The control plane functions as administration layer and is responsible for resource management, contract negotiation and administering data transfer.
The data plane does the heavy lifting of transferring and receiving data streams.

Each of these planes comes in several variants, allowing for example secrets to be stored in Azure Vault or a Hashicorp Vault.
These are described in detail in the [data plane overview](link here) and [control plane overview](link here).


    https://github.com/catenax-ng/product-edc/tree/develop/charts
    Difference between EDC Core vs. Tractus-X EDC
    Operations Mode Setup on K8 / Helm Chart
        Prerequisites e.g. Kubernetes
        Step by Step Guide
        Video (click through tutorial)

    All in One Deployment
        Step by Step Guide -> Transition from All-in-One Deployment to Transfer Data
        Examples / Video
        End to End Testing Video
    Outlook
        What about clients who dont want to use K8
        IaaC
        How to setup Gardner Stack (System Team?)