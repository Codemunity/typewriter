package templaters

import files.FileCrawler.{Result, Success}
import files.FileIO
import files.handlers.PageTemplateHandler
import models.{Config, Post}

import scala.concurrent.{ExecutionContext, Future}


class TagTemplater(workingDirectory: String) {

  def createTemplates(config: Config, posts: List[Post])(implicit ec: ExecutionContext): Future[Result] = {
    val mappedTags = extractTagsPerPost(posts)
    val f = mappedTags.map {
      case (tag, tagAndPosts) => evaluateTagTemplate(config, tag, tagAndPosts.map(_._2))
    }

    Future.sequence(f).map(_ => Success)
  }

  private def evaluateTagTemplate(config: Config, tag: String, posts: List[Post])(implicit ec: ExecutionContext): Future[Result] = {
    val context: Map[String, Map[String, Object]] = Map(
      "context" -> Map(
        "tag" -> tag.split(" ").map(_.capitalize).mkString(" "),
        "allPosts" -> posts.map(_.toMap)
      )
    )

    val template = s"$workingDirectory/${config.tagsTemplate}"
    val diff = FileIO.difference(workingDirectory, s"$workingDirectory/${FileIO.parentPath(config.tagsTemplate)}")
    val outputFile = s"$workingDirectory/${Config.buildDirName}/$diff/${tag.replace(" ", "-")}.html"

    println(s"TagTemplater: OUTPUT $outputFile")

    for {
      contents <- PageTemplater.createPageTemplate(workingDirectory, template, context)
      _ <- FileIO.write(contents, outputFile)
    } yield Success
  }

  private def extractTagsPerPost(posts: List[Post]): Map[String, List[(String, Post)]] =
    posts.flatMap(p => p.tags.map(t => (t, p))).groupBy(_._1)

}
