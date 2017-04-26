package files

import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by mlopezva on 1/6/17.
  */
class FileIOSpec extends WordSpecLike
  with MustMatchers {


  "A FileIO" must {

    "calculate the difference between a ascendant file and a child file correctly" in {
      val parent = "/my/path/to/parent"
      val child = "/my/path/to/parent/with/a/child"
      FileIO.difference(parent, child) mustBe "with/a/child"

      val useCaseParent = "/Users/mlopezva/Desktop/codemunity"
      val useCaseChild = "/Users/mlopezva/Desktop/codemunity/index"
      FileIO.difference(useCaseParent, useCaseChild) mustBe "index"
    }

  }

}
