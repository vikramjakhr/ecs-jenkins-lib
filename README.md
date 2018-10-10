# ECS-JENKINS-LIB

Jenkins shared library in groovy for deploying, restarting and wait for AWS ECS using jenkins pipeline. 


### Prerequisites

awscli and jq must be installed on the host system

```
pip install awscli
apt-get install jq
```

### Installing (Import) in Jenkins

Below is the step by step examples that tell you how to get it working

##### Goto configure system 

```
Manage Jenkins -> Configure System
```

##### Goto Global Pipeline Libraries 

```
* Scroll down to Global Pipeline Libraries section in cofiguration options.
* Click on Add button.
* Name the library (e.g. ecs-jenkins-lib)
* Set a default version (we can also specify this while importing library in pipeline)
* Now select the Modern SCM in Retrieval method section
* Select the Github option from Source Code Management section
* Now specify the github account and repository details
    * Credentials (any github user credential, required because github has pull request limits for
      annonymous users.
    * Specify owner as vikramjakhr  
    * Select the ecs-jenkins-lib from the dropdown
* Leave remaining options as it is.
```

![screenshot](https://raw.githubusercontent.com/vikramjakhr/ecs-jenkins-lib/master/slc.png)

### Using the library in pipeline

Currently library has 3 features: deploy, restart and wait 

##### 1) Deploy

This method deploys a new task definition to the specified service in a cluster.

Signature of the deploy method
```
def deploy(cluster, service, task_family, image, region, boolean is_wait = true, String awscli = "aws") {
    ...
}
```

Pipeline example:
```
// Import at the top in pipeline code as:
@Library('ecs-jenkins-lib@v1.0.0') _
    
    
// sample pipeline stage code
stage('Deploy') {
    steps {
        script {
            ecs.deploy("${env.CLUSTER_NAME}", "${env.SERVICE_NAME}", "${env.TASK_FAMILY}", "${env.IMAGE_TO_DEPLOY}", "${env.REGION}", true)
        }    
    }
}
```

##### 2) Restart

This method restart(redeploy) the containers in the specified service in a cluster.

Signature of the restart method
```
def restart(cluster, service, region, String awscli = "aws") {
    ...
}
```

Pipeline example:
```
// Import at the top in pipeline code as:
@Library('ecs-jenkins-lib@v1.0.0') _
    
    
// sample pipeline stage code
stage('Restart') {
    steps {
        script {
            ecs.restart("${env.CLUSTER_NAME}", "${env.SERVICE_NAME}", "${env.REGION}")
        }    
    }
}
```

##### 3) Wait

This method waits for until running count matches the desired count for a service.

Signature of the wait method
```
def wait(cluster, service, region, String awscli = "aws") {
    ...
}
```

Pipeline example:
```
// Import at the top in pipeline code as:
@Library('ecs-jenkins-lib@v1.0.0') _
    
    
// sample pipeline stage code
stage('Wait') {
    steps {
        script {
            ecs.wait("${env.CLUSTER_NAME}", "${env.SERVICE_NAME}", "${env.REGION}")
        }    
    }
}
```


## Contributing

You are welcome to contribute!

## Versioning

For the versions available, see the [tags on this repository](https://github.com/vikramjakhr/ecs-jenkins-lib/tags). 

## Authors

* **Vikram Jakhar** - *Initial work* - [vikramjakhr](https://github.com/vikramjakhr)
