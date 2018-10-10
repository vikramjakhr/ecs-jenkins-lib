// Restart ECS container

def restart(cluster, service, region, String awscli = "aws") {
    sh """
        ${awscli} ecs update-service \
            --cluster ${cluster} \
            --service ${service} \
            --force-new-deployment \
            --region "${region}"
    """
}

// Wait for changes to reflect
def wait(cluster, service, region, String awscli = "aws") {
    sh """
        ${awscli} ecs wait services-stable \
            --cluster ${cluster} \
            --service ${service} \
            --region "${region}"
    """
}