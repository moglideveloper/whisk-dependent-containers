package example.whisk

import com.github.dockerjava.core.DefaultDockerClientConfig
import com.whisk.docker.impl.dockerjava.{Docker, DockerJavaExecutorFactory}
import com.whisk.docker.{DockerContainer, DockerFactory, DockerKit}

import scala.concurrent.duration.DurationInt

object MultiContainerJavaDockerService extends DockerKit {

  private val docker = {
    val config = DefaultDockerClientConfig.createDefaultConfigBuilder.build
    new Docker(config)
  }

  override implicit val dockerFactory: DockerFactory =
    new DockerJavaExecutorFactory(docker)

  override val StartContainersTimeout = 5.minutes

  val appNetwork = "app"
  val tomcat: DockerContainer = DockerContainers.tomcatContainer(appNetwork)
  val mysql: DockerContainer = DockerContainers.mysqlContainer(appNetwork)

  override def dockerContainers: List[DockerContainer] = List(
    //IMPORTANT : mysql should be started before tomcat
    tomcat.withUnlinkedDependencies(mysql)
  )

  def main(args: Array[String]): Unit = {

    println("start of main")

    val containersString = dockerContainers.map(_.image).mkString(",")
    println("starting services >" + containersString + "<")

    DockerHelper.createNetworkIfNotExists(appNetwork, docker.client)
    startAllOrFail()

    println("end of main")
  }
}
