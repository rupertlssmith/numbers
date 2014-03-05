package org.scalatest.prop.nsv

import org.scalacheck.{Shrink, Prop, Gen}
import org.scalacheck.Prop._
import org.scalatest.prop.{DiscardedEvaluationException, Checkers, GeneratorDrivenPropertyChecks}

trait NoShrinkVariants extends GeneratorDrivenPropertyChecks
{
  def forAllNoShrink[A, B](genAndNameA: (Gen[A], String), genAndNameB: (Gen[B], String),
                           configParams: PropertyCheckConfigParam*)(fun: (A, B) => Unit)
                          (implicit
                           config: PropertyCheckConfig,
                           shrA: Shrink[A],
                           shrB: Shrink[B]
                            )
  {

    val (genA, nameA) = genAndNameA
    val (genB, nameB) = genAndNameB

    val propF =
    {
      (a: A, b: B) =>
        val (unmetCondition, exception) =
          try
          {
            fun(a, b)
            (false, None)
          }
          catch
            {
              case e: DiscardedEvaluationException => (true, None)
              case e: Throwable => (false, Some(e))
            }
        !unmetCondition ==> (
          if (exception.isEmpty) Prop.passed else Prop.exception(exception.get)
          )
    }
    val prop = Prop.forAllNoShrink(genA, genB)(propF)
    val params = getParams(configParams, config)
    Checkers.doCheck(prop, params, "GeneratorDrivenPropertyChecks.scala", "forAll", Some(List(nameA, nameB)))
  }

  def forAllNoShrink[A, B](genA: Gen[A], genB: Gen[B], configParams: PropertyCheckConfigParam*)(fun: (A, B) => Unit)
                          (implicit
                           config: PropertyCheckConfig,
                           shrA: Shrink[A],
                           shrB: Shrink[B]
                            )
  {
    val propF =
    {
      (a: A, b: B) =>
        val (unmetCondition, exception) =
          try
          {
            fun(a, b)
            (false, None)
          }
          catch
            {
              case e: DiscardedEvaluationException => (true, None)
              case e: Throwable => (false, Some(e))
            }
        !unmetCondition ==> (
          if (exception.isEmpty) Prop.passed else Prop.exception(exception.get)
          )
    }
    val prop = Prop.forAllNoShrink(genA, genB)(propF)
    val params = getParams(configParams, config)
    Checkers.doCheck(prop, params, "GeneratorDrivenPropertyChecks.scala", "forAll")
  }

  def forAllNoShrink[A, B, C](genAndNameA: (Gen[A], String), genAndNameB: (Gen[B], String), genAndNameC: (Gen[C], String), configParams: PropertyCheckConfigParam*)(fun: (A, B, C) => Unit)
                     (implicit
                      config: PropertyCheckConfig,
                      shrA: Shrink[A],
                      shrB: Shrink[B],
                      shrC: Shrink[C]
                       )
  {

    val (genA, nameA) = genAndNameA
    val (genB, nameB) = genAndNameB
    val (genC, nameC) = genAndNameC

    val propF =
    {
      (a: A, b: B, c: C) =>
        val (unmetCondition, exception) =
          try
          {
            fun(a, b, c)
            (false, None)
          }
          catch
            {
              case e: DiscardedEvaluationException => (true, None)
              case e: Throwable => (false, Some(e))
            }
        !unmetCondition ==> (
          if (exception.isEmpty) Prop.passed else Prop.exception(exception.get)
          )
    }
    val prop = Prop.forAllNoShrink(genA, genB, genC)(propF)
    val params = getParams(configParams, config)
    Checkers.doCheck(prop, params, "GeneratorDrivenPropertyChecks.scala", "forAll", Some(List(nameA, nameB, nameC)))
  }

  def forAllNoShrink[A, B, C, D](genAndNameA: (Gen[A], String), genAndNameB: (Gen[B], String), genAndNameC: (Gen[C],
    String), genAndNameD: (Gen[D], String), configParams: PropertyCheckConfigParam*)(fun: (A, B, C, D) => Unit)
                                (implicit
                                 config: PropertyCheckConfig,
                                 shrA: Shrink[A],
                                 shrB: Shrink[B],
                                 shrC: Shrink[C],
                                 shrD: Shrink[D]
                                  )
  {

    val (genA, nameA) = genAndNameA
    val (genB, nameB) = genAndNameB
    val (genC, nameC) = genAndNameC
    val (genD, nameD) = genAndNameD

    val propF =
    {
      (a: A, b: B, c: C, d: D) =>
        val (unmetCondition, exception) =
          try
          {
            fun(a, b, c, d)
            (false, None)
          }
          catch
            {
              case e: DiscardedEvaluationException => (true, None)
              case e: Throwable => (false, Some(e))
            }
        !unmetCondition ==> (
          if (exception.isEmpty) Prop.passed else Prop.exception(exception.get)
          )
    }
    val prop = Prop.forAllNoShrink(genA, genB, genC, genD)(propF)
    val params = getParams(configParams, config)
    Checkers.doCheck(prop, params, "GeneratorDrivenPropertyChecks.scala", "forAll", Some(List(nameA, nameB, nameC,
      nameD)))
  }

  def forAllNoShrink[A, B, C, D, E](genAndNameA: (Gen[A], String), genAndNameB: (Gen[B], String),
                                    genAndNameC: (Gen[C], String), genAndNameD: (Gen[D], String),
                                    genAndNameE: (Gen[E], String), configParams: PropertyCheckConfigParam*)(fun: (A,
    B, C, D, E) => Unit)
                                   (implicit
                                    config: PropertyCheckConfig,
                                    shrA: Shrink[A],
                                    shrB: Shrink[B],
                                    shrC: Shrink[C],
                                    shrD: Shrink[D],
                                    shrE: Shrink[E]
                                     )
  {

    val (genA, nameA) = genAndNameA
    val (genB, nameB) = genAndNameB
    val (genC, nameC) = genAndNameC
    val (genD, nameD) = genAndNameD
    val (genE, nameE) = genAndNameE

    val propF =
    {
      (a: A, b: B, c: C, d: D, e: E) =>
        val (unmetCondition, exception) =
          try
          {
            fun(a, b, c, d, e)
            (false, None)
          }
          catch
            {
              case e: DiscardedEvaluationException => (true, None)
              case e: Throwable => (false, Some(e))
            }
        !unmetCondition ==> (
          if (exception.isEmpty) Prop.passed else Prop.exception(exception.get)
          )
    }
    val prop = Prop.forAllNoShrink(genA, genB, genC, genD, genE)(propF)
    val params = getParams(configParams, config)
    Checkers.doCheck(prop, params, "GeneratorDrivenPropertyChecks.scala", "forAll", Some(List(nameA, nameB, nameC, nameD, nameE)))
  }
}
