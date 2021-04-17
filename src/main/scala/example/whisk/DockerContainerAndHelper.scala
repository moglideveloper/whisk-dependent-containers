package example.whisk

import com.github.dockerjava.api.DockerClient
import com.whisk.docker.{DockerContainer, DockerReadyChecker}

object DockerContainers {

  def mysqlContainer(networkName: String): DockerContainer = {
    DockerContainer("mysql", Option("example_database"))
      .withEnv(s"MYSQL_ROOT_PASSWORD=admin123")
      .withPorts(3306 -> Some(3306))
      .withNetworkMode(networkName)
      .withReadyChecker(
        DockerReadyChecker.LogLineContains(s"ready for connections")
      )
  }

  def tomcatContainer(networkName: String): DockerContainer = {
    DockerContainer("tomcat", Option("example_web_server"))
      .withPorts(8080 -> Some(8080))
      .withNetworkMode(networkName)
      .withReadyChecker(
        DockerReadyChecker.LogLineContains(s"Server startup in")
      )
  }
}

object DockerHelper {
  def createNetworkIfNotExists(
      networkName: String,
      dockerClient: DockerClient
  ): Unit = {
    val matchingNetworks =
      dockerClient.listNetworksCmd.withNameFilter(networkName).exec

    matchingNetworks.isEmpty match {
      case true =>
        val networkResponse = dockerClient.createNetworkCmd
          .withName(networkName)
          .withAttachable(true)
          .withDriver("bridge")
          .exec
        println(s"Network ${networkResponse.getId} created...\n")

      case false => println(s"$networkName already exists...\n")
    }
  }
}
