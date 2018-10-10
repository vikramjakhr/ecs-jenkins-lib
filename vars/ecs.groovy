/*
 * ecs.groovy
 * v1.0.0
 * 2018-10-10
 * Maintained and developed by @vikramjakhr
 */

/**
 * Deploy a task to specified service on the ecs cluster with task_family and image.
 * @param cluster : ECS cluster name
 * @param service : ECS cluster service name
 * @param task_family : Task family to update
 * @param image : Image to deploy
 * @param region : AWS region
 * @param is_wait : True if you want to wait until changes reflects completely
 * @param aws_cli : Pass the specific path (e.g. /usr/local/bin/aws) to aws cli. Default is aws.
 */
def deploy(cluster, service, task_family, image, region, boolean is_wait = true, String awscli = "aws") {
    sh """

        OLD_TASK_DEF=\$(${awscli} ecs describe-task-definition \
                                --task-definition ${task_family} \
                                --output json --region ${region})

        NEW_TASK_DEF=\$(echo \$OLD_TASK_DEF | \
                    jq --arg NDI ${image} '.taskDefinition.containerDefinitions[0].image=\$NDI')

        FINAL_TASK=\$(echo \$NEW_TASK_DEF | \
                    jq '.taskDefinition | \
                            {family: .family, \
                            networkMode: .networkMode, \
                            volumes: .volumes, \
                            containerDefinitions: .containerDefinitions, \
                            placementConstraints: .placementConstraints}')

        ${awscli} ecs register-task-definition \
                --family ${task_family} \
                --cli-input-json \
                "\$(echo \$FINAL_TASK)" --region "${region}"

        if [ \$? -eq 0 ]
        then
            echo "New task has been registered"
        else
            echo "Error in task registration"
            exit 1
        fi
        
        echo "Now deploying new version..."
                    
        ${awscli} ecs update-service \
            --cluster ${cluster} \
            --service ${service} \
            --force-new-deployment \
            --task-definition ${task_family} \
            --region "${region}"
        
        if ${is_wait}; then
            echo "Waiting for deployment to reflect changes"
            ${awscli} ecs wait services-stable \
                --cluster ${cluster} \
                --service ${service} \
                --region "${region}"
        fi
    """
}

/**
 * Restart the specified service on the ecs cluster.
 * @param cluster : ECS cluster name
 * @param service : ECS cluster service name
 * @param region : AWS region
 * @param aws_cli : Pass the specific path (e.g. /usr/local/bin/aws) to aws cli. Default is aws.
 */
def restart(cluster, service, region, String awscli = "aws") {
    sh """
        ${awscli} ecs update-service \
            --cluster ${cluster} \
            --service ${service} \
            --force-new-deployment \
            --region "${region}"
    """
}

/**
 * Wait for the deployed ecs changes to start reflecting. (Tries every 15 seconds)
 * @param cluster : ECS cluster name
 * @param region : AWS region
 * @param service : ECS cluster service name
 * @param aws_cli : Pass the specific path (e.g. /usr/local/bin/aws) to aws cli. Default is aws.
 */
def wait(cluster, service, region, String awscli = "aws") {
    sh """
        ${awscli} ecs wait services-stable \
            --cluster ${cluster} \
            --service ${service} \
            --region "${region}"
    """
}