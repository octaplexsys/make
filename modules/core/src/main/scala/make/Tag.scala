package make

import scala.reflect.runtime.universe.TypeTag
import make.internal.SourcePosMacro

final case class Tag[A](
  typeTag: TypeTag[A],
  sourcePos: Tag.SourcePos
)

object Tag {

  def of[A](implicit tag: Tag[A]): Tag[A] = tag

  implicit def tagFor[A](implicit typeTag: TypeTag[A], sourcePos: SourcePos): Tag[A] =
    Tag(typeTag, sourcePos)

  final case class SourcePos(path: String, line: Int, start: Int)
  object SourcePos {
    implicit def materialize: SourcePos =
      macro SourcePosMacro.materializeSourcePos
  }
}
