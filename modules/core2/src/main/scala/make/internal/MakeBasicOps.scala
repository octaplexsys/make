package make.internal

import make.Tag
import make.Make
import cats.Applicative
import cats.effect.Resource

trait MakeBasicOps {

  def map[F[_]: Applicative, A, B: Tag](ma: Make[F, A])(f: A => B): Make[F, B] =
    Make.Bind(
      ma,
      (a: A) => Resource.pure(f(a)),
      Tag.of[B]
    )

  def mapF[F[_]: Applicative, A, B: Tag](ma: Make[F, A])(f: A => F[B]): Make[F, B] =
    Make.Bind(
      ma,
      (a: A) => Resource.liftF(f(a)),
      Tag.of[B]
    )

  def mapResource[F[_], A, B: Tag](ma: Make[F, A])(f: A => Resource[F, B]): Make[F, B] =
    Make.Bind(
      ma,
      f,
      Tag.of[B]
    )

  def ap[F[_], A, B: Tag](ma: Make[F, A])(mf: Make[F, A => B]): Make[F, B] =
    Make.Ap(
      ma,
      mf,
      Tag.of[B]
    )

}