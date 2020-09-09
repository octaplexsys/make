package make

import cats.effect.Resource
import cats.Applicative
import make.internal.MakeOps
import cats.data.NonEmptyList
import cats.ApplicativeError
import cats.implicits._

object syntax extends MakeTupleSyntax {
  implicit def makeToBasicSyntax[F[_], A](make: Make[F, A]): MakeBasicSyntax[F, A] =
    new MakeBasicSyntax(make)
}

final class MakeBasicSyntax[F[_], A](private val m: Make[F, A]) extends AnyVal {
  def map[B: Tag](f: A => B)(implicit F: Applicative[F]): Make[F, B] =
    MakeOps.map(m)(f)

  def mapF[B: Tag](f: A => F[B])(implicit F: Applicative[F]): Make[F, B] =
    MakeOps.mapF(m)(f)

  def mapResource[B: Tag](f: A => Resource[F, B]): Make[F, B] =
    MakeOps.mapResource(m)(f)
  
  def ap[B: Tag](mf: Make[F, A => B]): Make[F, B] =
    MakeOps.ap(m)(mf)

  def toDag(implicit F: Applicative[F]): Either[NonEmptyList[Conflict], Dag[F, A]] =
    Dag.fromMake(m)

  def toResource(implicit F: ApplicativeError[F, Throwable]): Resource[F, A] = {
    toDag match {
      case Left(conflicts) =>
        // TODO
        val err = new Exception(s"Conflits: ${conflicts.map(_.toString).mkString_(",")}")
        val errF = F.raiseError(err)
        Resource.liftF(errF)
      case Right(dag) =>
        dag.toResource
    }
  }
}