package jgit.enrich

import git.{PullRequest, EnrichmentProvider}
import org.eclipse.jgit.lib.Repository
import jgit.JGitProvider._
import jgit.JGitExtensions._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.gitective.core.CommitUtils
import jgit.JGitProvider

/**
 * An info getter implementation for the JGit library.
 * @param provider The JGit provider.
 */
class JGitEnrichmentProvider(val provider: JGitProvider) extends EnrichmentProvider {
  val repo = provider.repository

  override def enrich(pullRequest: PullRequest): Future[PullRequest] = {
    Future {
      val head = repo resolve pullRef(pullRequest)
      val target = repo resolve targetRef(pullRequest)
      val base = if (head != null && target != null) CommitUtils.getBase(repo, head, target) else null

      // Check if commits are resolved
      if (head != null && base != null) {
        val (added, edited, deleted) = repo.diffSize(head, base)
        pullRequest.linesAdded = added + edited
        pullRequest.linesDeleted = deleted + edited
      }

      pullRequest
    }
  }
}
