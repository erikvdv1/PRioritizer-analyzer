package github

import git._
import dispatch.github.GitHub
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * A provider implementation for GitHub.
 * @param owner The name of the owner.
 * @param repository The name of the repository.
 * @param token The GitHub API access token.
 */
class GitHubProvider(val owner: String, val repository: String, token: String) extends Provider {

  override def repositoryProvider: Option[RepositoryProvider] = None
  override def pullRequestProvider: Option[GitHubPullRequestProvider] = Some(new GitHubPullRequestProvider(this))
  override def getDecorator(list: PullRequestList): PullRequestList = new GitHubDecorator(list, this)
  override def getPairwiseDecorator(list: PairwiseList): PairwiseList = list

  override def init(provider: PullRequestProvider = null): Future[Unit] = Future {
    // Set global access token
    GitHub.accessToken = token
  }

  override def dispose(): Unit = {
    GitHub.shutdown()
  }
}
