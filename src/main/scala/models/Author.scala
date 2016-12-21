package models

import net.jcazevedo.moultingyaml.DefaultYamlProtocol

/**
  * Created by mlopezva on 11/15/16.
  */
case class Author(
                   id: String,
                   name: String,
                   bio: String,
                   linkedIn: String,
                   twitter: String,
                   github: String,
                   profilePicture: String,
                   filepath: String
                 )

object AuthorFormat extends DefaultYamlProtocol {
  implicit val authorFormat = yamlFormat8(Author)
}
